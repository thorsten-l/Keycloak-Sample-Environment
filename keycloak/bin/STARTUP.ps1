Write-Host "Docker SHUTDOWN"
docker compose down

foreach ($fileName in 
  "kcldap\data\run\slapd-localhost.pid", 
  "kcldap\data\run\slapd-localhost.socket", 
  "kcdb\pg_stat_tmp" ) {
  Write-Host $fileName
  if ( Test-Path $fileName )
  {
    Remove-Item -Force -Recurse $fileName
  }
}

Write-Host "Docker STARTUP"
docker compose up -d
docker compose logs -f
