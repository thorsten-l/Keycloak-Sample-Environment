
### 1. Requirements

`/etc/hosts`

```text
127.0.0.1       id.dev.sonia.de
127.0.0.1       app1.dev.sonia.de
127.0.0.1       app2.dev.sonia.de
127.0.0.1       app3.dev.sonia.de
127.0.0.1       app4.dev.sonia.de
```

### 2. Client URLs

- http://app1.dev.sonia.de:8081
- http://app2.dev.sonia.de:8082
- http://app3.dev.sonia.de:8083
- http://app4.dev.sonia.de:8084


### 3. Credentials 

```text
389DS
DN: "cn=Directory Manager"
Password: "10peb8uW43kWQL519ibpsJoPpcrzPLY4tMAGBEf5e4.f8IipWmVE0A.3Gcpe0g2sw"

Keycloak admin : admin
Keycloak admin password : admin123

Test users : c1test[1-5]
Test users password : test123
```

### 5. Initial Settings

```bash
docker exec kcldap dsconf localhost config replace nsslapd-maxbersize=4194304
docker exec kcldap dsconf localhost config replace nsslapd-maxsasliosize=4194304
docker exec kcldap dsconf localhost config replace nsslapd-allow-anonymous-access=off
docker exec kcldap dsconf localhost config replace nsslapd-dynamic-plugins=on
docker exec kcldap dsconf localhost plugin memberof enable

docker exec kcldap dsconf localhost config get

#
# optional: disable syntax check if syntax errors on source are not correctable
#
docker exec kcldap dsconf localhost config replace nsslapd-syntaxcheck=off

#
# optional: change Directory Manager password
#
docker exec kcldap dsconf localhost config replace nsslapd-rootpw=<new password>

#
# optional: import existing server certificate
#
cp server.key server.crt ./data
docker exec kcldap dsctl localhost tls import-server-key-cert /data/server.crt /data/server.key
docker exec kcldap dsconf localhost security certificate list
rm ./data/server.key ./data/server.crt

#
# optional: change ports to standard 389/636
#
docker exec kcldap dsconf localhost config replace nsslapd-port=389
docker exec kcldap dsconf localhost config replace nsslapd-securePort=636
```

### 8. Create and initialize suffix

```bash
docker exec kcldap dsconf slapd-localhost backend create --suffix="dc=sample,dc=org" --be-name="sample"
docker exec kcldap dsidm -b "dc=sample,dc=org" slapd-localhost initialise
```

### 9. Configure uniqueness plugin for some attributes. here uid, mail and mailAlternateAddress

```bash
docker exec kcldap dsconf localhost plugin attr-uniq set --enabled on --attr-name uid mail mailAlternateAddress --subtree "dc=sample,dc=org" --across-all-subtrees on "attribute uniqueness"
docker exec kcldap dsconf localhost plugin attr-uniq show "attribute uniqueness"
```

### 12. Update and add indexes to improve performance

```bash
# update indexes
docker exec kcldap dsconf localhost backend index set --attr givenName --add-type approx sample
docker exec kcldap dsconf localhost backend index set --attr mail --add-type approx sample
docker exec kcldap dsconf localhost backend index set --attr mailAlternateAddress --add-type pres --add-type sub --add-type approx sample
docker exec kcldap dsconf localhost backend index set --attr uid --add-type pres --add-type sub sample

# add indexes
docker exec kcldap dsconf localhost backend index add --attr associatedDomain --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr departmentNumber --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr displayName --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr employeeNumber --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr employeetype --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr facsimileTelephoneNumber --index-type eq --index-type pres --index-type sub sample
docker exec kcldap dsconf localhost backend index add --attr gecos --index-type eq --index-type pres --index-type sub sample
docker exec kcldap dsconf localhost backend index add --attr gidnumber --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr icscalendar --index-type eq --index-type pres --index-type sub sample
docker exec kcldap dsconf localhost backend index add --attr icscalendarowned --index-type eq --index-type pres --index-type sub sample
docker exec kcldap dsconf localhost backend index add --attr icsstatus --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr inetCos --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr inetDomainBaseDN --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr jpegphoto --index-type eq sample
docker exec kcldap dsconf localhost backend index add --attr l --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr mailDomainStatus --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr mailEquivalentAddress --index-type eq --index-type pres --index-type sub --index-type approx sample
docker exec kcldap dsconf localhost backend index add --attr mailUserStatus --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr memberUid --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr nsRoleDN --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr o --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr ou --index-type eq --index-type pres sample
docker exec kcldap dsconf localhost backend index add --attr uidnumber --index-type eq --index-type pres sample

# list indexes
docker exec kcldap dsconf localhost backend index list sample

# reindex
docker exec kcldap dsconf localhost backend index reindex sample

# observe reindexing progress in log
docker logs -f 389ds
```

### 13. Re-adjust performance parameters: see

> [Tuning the performance](https://access.redhat.com/documentation/en-us/red_hat_directory_server/12/html-single/tuning_the_performance_of_red_hat_directory_server),
> [Administration Guide](https://access.redhat.com/documentation/en-us/red_hat_directory_server/11/html-single/administration_guide/index)

```bash
# search size limit: specifies the maximum number of entries to return from a search operation
docker exec kcldap dsconf slapd-localhost config replace nsslapd-sizelimit=50000

# Index Scan Limit: specifies the maximum number of entry IDs loaded from an index file for search results, it can make a search treated as an unindexed.
docker exec kcldap dsconf slapd-localhost backend config set --idlistscanlimit=50000

# Lookthrough Limit: specifies how many entries are examined for a search operation
docker exec kcldap dsconf slapd-localhost backend config set --lookthroughlimit=50000
```

### 14. Configure and limit logging rotation

```bash
# configure error log to keep maximum 5 logs and to rotate log files with a 100 MB size or every 1 week, enter:
docker exec kcldap dsconf localhost config replace nsslapd-errorlog-maxlogsperdir=5 nsslapd-errorlog-maxlogsize=100 nsslapd-errorlog-logrotationtime=1 nsslapd-errorlog-logrotationtimeunit=week
docker exec kcldap dsconf slapd-localhost config get | grep -i errorlog

# configure access log to keep maximum 10 logs and to rotate log files with a 100 MB size or every 1 week, enter:
docker exec kcldap dsconf localhost config replace nsslapd-accesslog-maxlogsperdir=10 nsslapd-accesslog-maxlogsize=100 nsslapd-accesslog-logrotationtime=1 nsslapd-accesslog-logrotationtimeunit=week
docker exec kcldap dsconf slapd-localhost config get | grep -i accesslog
```
