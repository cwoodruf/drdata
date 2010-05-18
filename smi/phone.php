<?php

define('__SMI__',false);
header("content-type: text/plain");

include_once('lib/.localsettings.php');
require_once('.db/check.php');
require_once('lib/model.php');
require_once('lib/controller.php');
require_once('lib/phone-model.php');
require_once('lib/phone-controller.php');

$action = new PhoneAction;
print $action->process();
