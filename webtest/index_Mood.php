<?php
$couch_dsn = "http://115.146.87.248:5984/";
$couch_db = "summary";


require_once "./couch.php";
require_once "./couchClient.php";
require_once "./couchDocument.php";

$client = new couchClient($couch_dsn,$couch_db);

$q=$_GET["q"];

if ($q=="first_query")
{
  try {
	$data = $client->group_level(2)->getView('function','getHappinessBy24HourByDistricts');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}

$day=array
(
 array("Time","Bronx","Brooklyn","Manhattan","Queens","Staten Island"),
 array(0,0,0,0,0,0),
 array(1,0,0,0,0,0),
 array(2,0,0,0,0,0),
 array(3,0,0,0,0,0),
 array(4,0,0,0,0,0),
 array(5,0,0,0,0,0),
 array(6,0,0,0,0,0),
 array(7,0,0,0,0,0),
 array(8,0,0,0,0,0),
 array(9,0,0,0,0,0),
 array(10,0,0,0,0,0),
 array(11,0,0,0,0,0),
 array(12,0,0,0,0,0),
 array(13,0,0,0,0,0),
 array(14,0,0,0,0,0),
 array(15,0,0,0,0,0),
 array(16,0,0,0,0,0),
 array(17,0,0,0,0,0),
 array(18,0,0,0,0,0),
 array(19,0,0,0,0,0),
 array(20,0,0,0,0,0),
 array(21,0,0,0,0,0),
 array(22,0,0,0,0,0),
 array(23,0,0,0,0,0)
);
$temp=$data->rows;

for($area=1; $area<6; $area++){
 for($i=0; $i<120; $i++){
  $key=$temp[$i]->key;
  $value=$temp[$i]->value;
   for($hour=0; $hour<24; $hour++){  
    if($key[0]==$day[0][$area]&&$key[1]==$day[$hour+1][0]){
      $day[$hour+1][$area]=$value[0];
   }
  }
 }
}

echo json_encode($day);
}






if ($q=="second_query")
{
  try {
	$data = $client->group_level(1)->getView('function','getHappinessBy24HourByDistricts');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}

$onmap=array(0,0,0,0,0);
$temp=$data->rows;
for($p=0;$p<5;$p++){
 $value=$temp[$p]->value;
 $onmap[$p]=$value[0];
}

echo json_encode($onmap);
}





if ($q=="third_query")
{
  try {
	$data = $client->group_level(1)->getView('function','getHappinessNewYork24Hour');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}

$day=array
(
 array("Time","New York"),
 array(0,0),
 array(1,0),
 array(2,0),
 array(3,0),
 array(4,0),
 array(5,0),
 array(6,0),
 array(7,0),
 array(8,0),
 array(9,0),
 array(10,0),
 array(11,0),
 array(12,0),
 array(13,0),
 array(14,0),
 array(15,0),
 array(16,0),
 array(17,0),
 array(18,0),
 array(19,0),
 array(20,0),
 array(21,0),
 array(22,0),
 array(23,0)
);
$temp=$data->rows;

for($hour=0; $hour<24; $hour++){  
 $key=$temp[$hour]->key;
 $value=$temp[$hour]->value;
 $day[$hour+1][1]=$value[0];
}
echo json_encode($day);
}

?>
