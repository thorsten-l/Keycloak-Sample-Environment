/*
 * Copyright 2024 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package l9g.webapp.nativeoidc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OidcDiscovery(
    String issuer,
    @JsonProperty("authorization_endpoint") String authorizationEndpoint,
    @JsonProperty("token_endpoint") String tokenEndpoint,
    @JsonProperty("introspection_endpoint") String introspectionEndpoint,
    @JsonProperty("userinfo_endpoint") String userinfoEndpoint,
    @JsonProperty("end_session_endpoint") String endSessionEndpoint,
    @JsonProperty("frontchannel_logout_session_supported") boolean frontchannelLogoutSessionSupported,
    @JsonProperty("frontchannel_logout_supported") boolean frontchannelLogoutSupported,
    @JsonProperty("jwks_uri") String jwksUri,
    @JsonProperty("check_session_iframe") String checkSessionIframe,
    @JsonProperty("grant_types_supported") List<String> grantTypesTupported
) {}

