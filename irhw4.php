<?php
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
if ($query)
{
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a Lucene
  // php include path entry in the php.ini)
  require_once('/opt/lampp/htdocs/solr-php-client-master/Apache/Solr/Service.php');
  // create a new solr service instance - host, port, and webapp
  // path (all Lucenes in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample');
  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }
  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
$selection = isset($_REQUEST['sort'])? $_REQUEST['sort'] : "Lucene";
  try
  {
  if($selection == "Lucene"){
     $arrparam=array('sort' => '');
}
    else{
    $arrparam = array('sort' => 'pageRankFile desc');
}
    $results = $solr->search($query, 0, $limit, $arrparam);
  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}
?>
<html>
  <head>
    <title>Indexing Web using Solr</title>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get" >
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <input type="submit" value="Submit"/>
<br/>
    <input type="radio" name="sort" value="Lucene" <?php if(isset($_REQUEST['sort']) && $selection == "Lucene") { echo 'checked="checked"';} ?>>Lucene
    <input type="radio" name="sort" value="pagerank" <?php if(isset($_REQUEST['sort']) && $selection == "pagerank") { echo 'checked="checked"';} ?>>Page Rank
    </form>
<?php
if ($results)
{
  $num = (int) $results->response->numFound;
  $start = min(1, $num);
  $end = min($limit, $num);
  $stack = [];
echo "  <div>Results $start -  $end of $num :</div> <ol>";
$csvData =  array_map('str_getcsv', file('UrlToHtml_mercury.csv'));
foreach ($results->response->docs as $doc)
  {  
    $id = $doc->id;
    $title = $doc->title;
   $desc = $doc->description;
   $url = $doc->og_url;
   if($desc=="" ||$desc==null){
     $desc = "N/A";
	}
     if($title=="" ||$title==null){
       $title="N/A";
   }
   
   	if($url == "" || $url == null)
	{
	foreach($csvData as $line)
		{
			$check = "/home/sachin/shared/solr-7.5.0/solr-7.5.0/crawl_data/".$line[0];
			if($check == $id)
			{
				$url = $line[1];
				unset($line);
				break;
			}
		}
	}
	echo "Title : <a href = '$url'>$title</a></br>";
	echo	"URL : <a href = '$url'>$url</a></br>";
	echo	"ID : $id</br>";
	echo	"Description : $desc </br></br>";
	}
}
?>

  </body>
</html>
