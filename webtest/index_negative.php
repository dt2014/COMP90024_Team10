<?php // negative people
$couch_dsn = "http://115.146.87.248:5984/";
$couch_db = "summary";

require_once "./couch.php";
require_once "./couchClient.php";
require_once "./couchDocument.php";

$client = new couchClient($couch_dsn,$couch_db);

$q=$_GET["q"];

if ($q=="socialmedia")
{
  try {
	$data = $client->group_level(1)->getView('function','getCelebrityNegative');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;
$len_temp=count($temp);
$blacklist=array();

for($h=0; $h<$len_temp; $h++)
{
 $key=$temp[$h]->key;
 $value=$temp[$h]->value;
 if($value>5){array_push($blacklist,[$key,$value]);}
}

function arr_sort($array,$key)
{
$arr_nums=$arr=array();
foreach($array as $k=>$v){$arr_nums[$k]=$v[$key];}
arsort($arr_nums);
foreach($arr_nums as $k=>$v){$arr[$k]=$array[$k];}
return $arr;
}
$sorted=array_values(arr_sort($blacklist,1));
$sortedblacklist=array();

for($v=0; $v<10; $v++){
 array_push($sortedblacklist,$sorted[$v]);
}

echo json_encode($sortedblacklist);
}

?>
