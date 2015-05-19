<?php
$couch_dsn = "http://115.146.87.248:5984/";
$couch_db = "summary";
require_once "./couch.php";
require_once "./couchClient.php";
require_once "./couchDocument.php";

$client = new couchClient($couch_dsn,$couch_db);

$q=$_GET["q"];


if ($q=="hero")
{
  try {
	$data = $client->group_level(1)->getView('function','getSuperhero');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;

$hero=array
(
 array("Name","Fans"),
 array("Antman",0),
 array("Aquaman",0),
 array("Batman",0),
 array("Black Widow",0),
 array("Blink",0),
 array("Captain America",0),
 array("Catwoman",0),
 array("Doctor Who",0),
 array("Dr. Strange",0),
 array("Green Arrow",0),
 array("Green Lantern",0),
 array("Groot",0),
 array("Hulk",0),
 array("Iceman",0),
 array("Iron Man",0),
 array("spider man",0),
 array("Spider-Man",0),
 array("Superman",0),
 array("Thor",0),
 array("Wolverine",0),
 array("Wonder Woman",0)
);

for($h=0; $h<21; $h++){  
 $key=$temp[$h]->key;
 $value=$temp[$h]->value;
 if($hero[$h+1][0]==$key){
 $hero[$h+1][1]=$value;
 }
}
echo json_encode($hero);
}




if ($q=="player")
{
  try {
	$data = $client->group_level(1)->getView('function','getBasketball');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;

$player=array
(
 array("Name","Fans"),
 array("76ers",0),array("Blazers",0),array("Bucks",0),
 array("Bulls",0),array("Cavaliers",0),array("Celtics",0),
 array("Clippers",0),array("Grizzlies",0),array("Hawks",0),
 array("Heat",0),array("Hornets",0),array("Jazz",0),
 array("Kings",0),array("Knicks",0),array("Lakers",0),
 array("Magic",0),array("Mavericks",0),array("Nets",0),
 array("Nuggets",0),array("Pacers",0),array("Pelicans",0),
 array("Pistons",0),array("Raptors",0),array("Rockets",0),
 array("Spurs",0),array("Suns",0),array("Thunder",0),
 array("Timberwolves",0),array("Warriors",0),array("Wizards",0)
);

for($h=0; $h<30; $h++){  
 $key=$temp[$h]->key;
 $value=$temp[$h]->value;
 if($player[$h+1][0]==$key){
 $player[$h+1][1]=$value;
 }
}
echo json_encode($player);
}




if ($q=="phone")
{
  try {
	$data = $client->group_level(1)->getView('function','getIPhoneAndroid');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;
$phone=array
(
 array("OS","Count"),
 array("Android",0),
 array("iPhone",0)
);

for($h=0; $h<2; $h++){  
 $key=$temp[$h]->key;
 $value=$temp[$h]->value;
 $phone[$h+1][1]=$value;
}
echo json_encode($phone);
}



if ($q=="president")
{
  try {
	$data = $client->group_level(1)->getView('function','getUSPresidentialElection');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;

$president=array
(
 array("Candidate","support"),
 array("Ben Carson",0),
 array("Bernie Sanders",0),
 array("Carly Fiorina",0),
 array("Hillary Clinton",0),
 array("Mike Huckabee",0),
 array("Rand Paul",0),
 array("Ted Cruz",0)
);


for($h=0; $h<7; $h++){ 
 $key=$temp[$h]->key;
 $value=$temp[$h]->value;
 if($president[$h+1][0]==$key){
 $president[$h+1][1]=$value;
 }
}


echo json_encode($president);
}

?>
