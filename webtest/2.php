<?php
$couch_dsn = "http://115.146.87.248:5984/";
$couch_db = "stream_ny";
require_once "./couch.php";
require_once "./couchClient.php";
require_once "./couchDocument.php";

$client = new couchClient($couch_dsn,$couch_db);

try {
	$response = $client->descending(TRUE)->limit(1)->include_docs(TRUE)->getView('function','getID');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$row=$response->rows;
$temp=$row[0]->doc;
$realtime=array();

//text
$text=$temp->text;

//name
$na=$temp->user;
$name=$na->screen_name;

//location
$p=$temp->place;
$place=$p->name;

$loc=$temp->geo;
$location=$loc->coordinates;

if($location==null&&$place=="Manhattan"){
 $location=[40.789741, -73.968373];
}
elseif($location==null&&$place=="Queens"){
 $location=[40.651018, -73.777465];
}
elseif($location==null&&$place=="Brooklyn"){
 $location=[40.637429, -73.983793];
}
elseif($location==null&&$place=="Bronx"){
 $location=[40.827902, -73.863245];
}
elseif($location==null&&$place=="Staten Island"){
 $location=[40.615184, -74.081930];
}
else{
 $location=[40.747024, -74.054525];
}

array_push($realtime,$name, $text, $location);

echo json_encode($realtime);
?>
