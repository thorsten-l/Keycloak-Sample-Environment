<?php
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

require 'vendor/autoload.php';
require_once 'config.php';
require_once 'OidcService.php';

use App\OidcService;

error_log("/oidc-backchannel-logout called");

$oidc = new OidcService($config);

$logoutToken = file_get_contents('php://input');
error_log("handleBackchannelLogout logoutToken=" . $logoutToken);

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
	if (!empty($logoutToken)) {
		$logoutTokenData = $oidc->decodeJwt($logoutToken);
		$sid = $logoutTokenData['sid'];
		error_log("handleBackchannelLogout logoutTokenData sid=" . $sid);

		$redis = new Redis();
    $redis->connect($config['redis']['host'], $config['redis']['port']);
    $r_session_id = $redis->get($sid);
		$redis->del($sid);

		error_log("handleBackchannelLogout r_session_id=" . $r_session_id);
	
		session_id($r_session_id);
		session_start();
		session_destroy();
		error_log("Session with sid=" . $r_session_id . " destroyed.");

		http_response_code(200);
		echo "Logout token processed successfully.";
	} else {
		http_response_code(400);
		echo "Bad Request: No logout token provided.";
	}
} else {
	http_response_code(405);
	echo "Method Not Allowed: Only POST requests are allowed.";
}
