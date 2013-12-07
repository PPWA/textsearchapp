$(init); 

function init(){ 
	$.getJSON("/new-topics", function (data){ 
		$.each(data.articles, function(i,item) { 
			//alert("sdf"); 
			$("#output").append("<div>" + item["art_date"] + "</div>"); 
			$("#output").append("<div>" + item["art_title"] + "</div>");
			$("#output").append("<div>" + item["art_teaser"] + "</div>");
			$("#output").append("<div>" + item["art_urlsource"] + "</div>");
			$("#output").append("<div>" + item["art_picture"] + "</div>");
			$("#output").append("<div>" + item["art_newportal"] + "</div>");
			$("#output").append("<div>" + item["art_topicHash"] + "</div>");
			$("#output").append("<div>" + item["art_explanation"] + "</div>" + "<br>");
						
			}); 
		}); 
	}

function refresh(){
	init();
}

/*
$(init); 
function init(){ 
	$.getJSON("/new-topics", function (data){ 
		$.each(data.articles, function(i,item) { 
			$("#output").append(item["art_date"]); 
			}); 
		}); 
	}
*/