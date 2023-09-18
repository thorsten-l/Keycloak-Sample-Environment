// OIDC configuration
var oidcConfig = {
    authority: 'https://id.dev.sonia.de/realms/dev',
    client_id: 'app11',
    client_secret: '5G7HBlAokSTQPhSRyX3ou9ytfZQPXw77',
    redirect_uri: window.location.origin + '/callback.html',
    response_type: 'code',
    scope: 'openid profile email',
};
