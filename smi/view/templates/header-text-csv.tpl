{php}
global $studyname;
header("content-type: text/csv");
header("content-disposition: attachment; filename=data-$studyname.csv");
{/php}
