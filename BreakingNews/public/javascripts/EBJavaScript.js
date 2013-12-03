/*
$(init);
function init(){
$.getJSON("article.json", processResult);
}

function processResult(data){
$("#output").text(" ");
for (articleTitle in data){
var article = data[articleTitle];
$("#output").append("<h2>" + articleTitle + "<h2>");
$("#output").append("<dl>");
for (detail in article){
$("#output").append("<dt>" + detail + "</dt>");
$("#output").append("<dd>" + article[detail] + "</dd>");
} //Ende for
$("#output").append("</dl>");
} //Ende for
}// Ende processResult

*/