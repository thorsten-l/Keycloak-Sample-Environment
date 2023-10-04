const appTitle = 'App12 - Flutter OIDC AppAuth';

// ID.DEV
//const oidcDiscoveryUrl = "https://id.dev.sonia.de/realms/dev/.well-known/openid-configuration";
//const oidcClientSecret = "0Jv69f5uaX14Xuel0ScDvGVVwXgTL6LM"; // id.dev
//const loginHost = "Login via id.dev.sonia.de";
//const loginText = "User: c1test1 / Password: test123";

// ID.QA
const oidcDiscoveryUrl = "https://id.qa.sonia.de/realms/dev/.well-known/openid-configuration";
const oidcClientSecret = "Ds3EJSvyddeYRvhugaeHgYY83E25YElw"; // id.qa
const loginHost = "Login via id.qa.sonia.de";
const loginText = "User: test1 / Password: test123";

const oidcClientId = "app12";
const oidcRedirectUrl = "com.example.app12://callback";

const authorizedKey = "authorized";
const idTokenKey = "idToken";
const accessTokenKey = "accessToken";
const refreshTokenKey = "refreshToken";

const githubUrl =
    "https://github.com/thorsten-l/Keycloak-Sample-Environment/tree/main/samples/app12-flutter-appauth";