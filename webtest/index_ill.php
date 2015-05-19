<?php //ill people cluster
$couch_dsn = "http://115.146.87.248:5984/";
$couch_db = "summary";


require_once "./couch.php";
require_once "./couchClient.php";
require_once "./couchDocument.php";

$client = new couchClient($couch_dsn,$couch_db);

$q=$_GET["q"];
if ($q=="ill")
{
  try {
	$data = $client->group_level(2)->getView('function','getHospitalInNeed');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;
$len_center=count($temp);

$cluster=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>11){
  array_push($cluster,[$key,$value]);
 }
}

echo json_encode($cluster);
}
?>
