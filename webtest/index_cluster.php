<?php // people cluster by language
$couch_dsn = "http://115.146.87.248:5984/";
$couch_db = "summary";

require_once "./couch.php";
require_once "./couchClient.php";
require_once "./couchDocument.php";

$client = new couchClient($couch_dsn,$couch_db);

$q=$_GET["q"];


  try {
	$data = $client->group_level(2)->getView('function','getMixCulture');}
  catch (Exception $e) {
	if ( $e->code() == 404 ) {echo "Document not found\n";}
        else {echo "Error: ".$e->getMessage()." (errcode=".$e->getCode()."\n";}
	exit(1);}
$temp=$data->rows;
$len_center=count($temp);


if ($q=="ar")
{
$cluster_ar=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>140&&$key[0]=="ar"){
  array_push($cluster_ar,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_ar);
}

if ($q=="es")
{
$cluster_es=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>800&&$key[0]=="es"){
  array_push($cluster_es,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_es);
}


if ($q=="fr")
{
$cluster_fr=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>220&&$key[0]=="fr"){
  array_push($cluster_fr,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_fr);
}


if ($q=="zh")
{
$cluster_zh=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>9&&$key[0]=="zh"){
  array_push($cluster_zh,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_zh);
}

if ($q=="ja")
{
$cluster_ja=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>35&&$key[0]=="ja"){
  array_push($cluster_ja,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_ja);
}


if ($q=="ko")
{
$cluster_ko=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>15&&$key[0]=="ko"){
  array_push($cluster_ko,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_ko);
}

if ($q=="de")
{
$cluster_de=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>50&&$key[0]=="de"){
  array_push($cluster_de,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_de);
}

if ($q=="pt")
{
$cluster_pt=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>130&&$key[0]=="pt"){
  array_push($cluster_pt,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_pt);
}

if ($q=="nl")
{
$cluster_nl=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>39&&$key[0]=="nl"){
  array_push($cluster_nl,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_nl);
}

if ($q=="ru")
{
$cluster_ru=array();
for($i=0;$i<$len_center;$i++){
 $key=$temp[$i]->key;
 $value=$temp[$i]->value;
 if($value>30&&$key[0]=="ru"){
  array_push($cluster_ru,[$key[0],$key[1],$value]);
 }
}
echo json_encode($cluster_ru);
}

?>
