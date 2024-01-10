package main

import (
	"context"
	"encoding/base64"
	"encoding/json"
	"errors"
	"fmt"
	"html/template"
	"net/http"
	"net/url"
	"os"
	"strings"
	"time"

	"github.com/gorilla/sessions"

	"golang.org/x/oauth2"
)

type UserInfo struct {
	Aud               string
	Iss               string
	PreferredUsername string `json:"preferred_username"`
	ZoneInfo          string
	Nickname          string
	Email             string
	Name              string
	GivenName         string `json:"given_name"`
	MiddleName        string `json:"middle_name"`
	FamilyName        string `json:"family_name"`
	Locale            string
	Birthdate         string
	Gender            string
}

var (
	oauth2Config *oauth2.Config
	tmpl         *template.Template
)

var store = sessions.NewCookieStore([]byte("Ihr-sehr-geheimer-Schlüssel"))

func main() {

	oauth2Config = &oauth2.Config{
		ClientID:     "app13",
		ClientSecret: "DJp129cs9DjMvGp1Ol4t3FYoqdDs1YuF",
		RedirectURL:  "http://app13.dev.sonia.de:8093/callback",
		Scopes:       []string{"openid", "profile", "email"},
		Endpoint: oauth2.Endpoint{
			AuthURL:  "https://id.dev.sonia.de/realms/dev/protocol/openid-connect/auth",
			TokenURL: "https://id.dev.sonia.de/realms/dev/protocol/openid-connect/token",
		},
	}

	tmpl = template.Must(template.ParseGlob("templates/*.html"))

	port := os.Getenv("PORT")
	if port == "" {
		port = "8093"
	}

	http.HandleFunc("/", welcomeHandler)
	http.HandleFunc("/protected/info", authMiddleware(infoHandler))
	http.HandleFunc("/protected/account", authMiddleware(accountHandler))
	http.HandleFunc("/callback", callbackHandler)
	http.HandleFunc("/logout", logoutHandler)

	fmt.Println("Starting server ...")

	http.ListenAndServe(":"+port, nil)
}

func welcomeHandler(w http.ResponseWriter, r *http.Request) {
	executeTemplate(w, "index.html", "Willkommen", "Willkommen auf der Startseite!")
}

func infoHandler(w http.ResponseWriter, r *http.Request) {
	executeTemplate(w, "info.html", "Info-Seite", "Das aktuelle Datum ist "+time.Now().Format("2006-01-02"))
}

func postLogoutRequest(idToken, redirectURI string) error {

	keycloakLogoutURL := "https://id.dev.sonia.de/realms/dev/protocol/openid-connect/logout"

	formData := url.Values{}
	formData.Set("client_id", "app13")
	formData.Set("id_token_hint", idToken)
	formData.Set("post_logout_redirect_uri", redirectURI)

	resp, err := http.PostForm(keycloakLogoutURL, formData)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	return nil
}

func logoutHandler(w http.ResponseWriter, r *http.Request) {

	fmt.Println("Logout")

	session, err := store.Get(r, "session-name")
	if err != nil {
		return
	}

	idToken, ok := session.Values["idToken"].(string)
	if !ok {
		http.Error(w, "ID Token nicht gefunden", http.StatusBadRequest)
		return
	}

	session.Values["authenticated"] = false
	session.Values["idToken"] = ""
	session.Options.MaxAge = -1

	err = session.Save(r, w)
	if err != nil {
		http.Error(w, "Fehler beim Speichern der Session", http.StatusInternalServerError)
		return
	}

	err = postLogoutRequest(idToken, "http://app13.dev.sonia.de:8093/")
	if err != nil {
		http.Error(w, "Fehler beim Senden des Logout-Requests", http.StatusInternalServerError)
		return
	}

	http.Redirect(w, r, "/", http.StatusFound)
}

func accountHandler(w http.ResponseWriter, r *http.Request) {

	session, err := store.Get(r, "session-name")
	if err != nil {
		http.Error(w, "Fehler beim Abrufen der Session", http.StatusInternalServerError)
		return
	}

	userInfo, err := getUserInfoFromIDToken(session.Values["idToken"].(string))
	if err != nil {
		http.Error(w, "Fehler beim Extrahieren der Benutzerinformationen", http.StatusInternalServerError)
		return
	}

	executeTemplate(w, "account.html", "Account-Seite", "Ihr Benutzername ist ... "+userInfo.PreferredUsername)
}

func callbackHandler(w http.ResponseWriter, r *http.Request) {
	fmt.Println("callbackHandler")

	code := r.URL.Query().Get("code")
	if code == "" {
		http.Error(w, "Code nicht gefunden", http.StatusBadRequest)
		return
	}

	token, err := oauth2Config.Exchange(context.Background(), code)
	if err != nil {
		http.Error(w, "Fehler beim Austausch des Codes", http.StatusInternalServerError)
		return
	}

	idToken, ok := token.Extra("id_token").(string)
	if !ok {
		http.Error(w, "ID Token nicht gefunden", http.StatusInternalServerError)
		return
	}

	session, err := store.Get(r, "session-name")
	if err != nil {
		http.Error(w, "Fehler beim Abrufen der Session", http.StatusInternalServerError)
		return
	}

	session.Values["authenticated"] = true
	session.Values["idToken"] = idToken

	err = session.Save(r, w)
	if err != nil {
		http.Error(w, "Fehler beim Speichern der Session", http.StatusInternalServerError)
		return
	}

	http.Redirect(w, r, "/protected/account", http.StatusFound)
}

func getUserInfoFromIDToken(idToken string) (UserInfo, error) {

	parts := strings.Split(idToken, ".")
	if len(parts) != 3 {
		return UserInfo{}, errors.New("invalid token format")
	}

	payload, err := base64.RawURLEncoding.DecodeString(parts[1])
	if err != nil {
		return UserInfo{}, err
	}

	fmt.Printf("idToken payload=%s\n", string(payload))

	var userInfo UserInfo
	err = json.Unmarshal(payload, &userInfo)
	if err != nil {
		fmt.Println("Error unmarshalling")
		return UserInfo{}, err
	}

	fmt.Printf("userInfo=%v\n", userInfo)
	return userInfo, nil
}

func authMiddleware(next http.HandlerFunc) http.HandlerFunc {

	return func(w http.ResponseWriter, r *http.Request) {

		session, err := store.Get(r, "session-name")
		if err != nil {
			http.Error(w, "Interner Serverfehler", http.StatusInternalServerError)
			return
		}

		if auth, ok := session.Values["authenticated"].(bool); !ok || !auth {
			redirectURL := oauth2Config.AuthCodeURL("some-random-state")
			http.Redirect(w, r, redirectURL, http.StatusFound)
			return
		}

		next.ServeHTTP(w, r)
	}
}

func executeTemplate(w http.ResponseWriter, tmplName, title, message string) {
	data := struct {
		Title   string
		Message string
	}{
		Title:   title,
		Message: message,
	}
	tmpl.ExecuteTemplate(w, tmplName, data)
}
