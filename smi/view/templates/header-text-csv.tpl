{php}
global $studyname;
header("content-type: text/csv");
header("content-disposition: attachment; name=data-$studyname.csv");
{/php}
