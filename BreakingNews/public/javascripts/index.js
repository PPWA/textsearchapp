
$(init); 
$(oldArticles)

function init(){ 
	$("#main").html("");	
	$.getJSON("/new-topics", function (data){ 
		$.each(data.articles, function(i,item) { 

			var curArticle = " ";
			
			curArticle = 				
				
				'<div class="art_header">'+
					'<a href="'+ item["art_urlsource"]+ '">' +
						'<img class="img_rss" src="/assets/images/rss.png" alt="rss" />'+
						'<h2>'+ item["art_title"] +'</h2>'+
					'</a>'+
				'</div>'
				+ 
				'<img class="art_img" src="'+ item["art_urlpicture"]+ '" alt="" />'
				+ 
				'<div class="art_main">'+
					'<a href="'+ item["art_urlsource"]+ '" >'+'<p class="art_newsportal">'+ item["art_newportal"] +'</p>'+'</a>'+
					'<p class="teaser">'+ item["art_teaser"] +'</p>'+
				'</div>'
				+ 
				'<div class="art_time">'+'<p>'+ item["art_date"]+ '</p>'+'</div>'
				+
				'<div style="clear:both;">'+'</div>';
				

				//alert("test2");
			
			$("#main").append("<article>" + curArticle + "</article>");

			

			/*
			 * 	+
				'<div class="art_similar">'+'<a href="">'+'<p>'+ 
					'<img src="images/plus.png" alt="" />'+ item["art_similar"]'</p>'+'</a>'+'</div>'
				
			$("#output").append("<div>" + item["art_date"] + "</div>"); 
			$("#output").append("<div>" + item["art_title"] + "</div>");
			$("#output").append("<div>" + item["art_teaser"] + "</div>");
			$("#output").append("<div>" + item["art_urlsource"] + "</div>");
			$("#output").append("<div>" + item["art_urlpicture"] + "</div>");
			$("#output").append("<div>" + item["art_newportal"] + "</div>");
			$("#output").append("<div>" + item["art_topicHash"] + "</div>");
			$("#output").append("<div>" + item["art_explanation"] + "</div>" + "<br>");
			*/	

			}); 
		}); 
	 
}


function oldArticles(){
$.getJSON("/new-topics", function (data){ 
	$.each(data.articles, function(i,item) { 
	
		var oldArticle = " ";
		
		oldArticle = 				
			
			'<div class="art_header">'+
				'<a href="'+ item["art_urlsource"]+ '">' +
					'<img class="img_rss" src="/assets/images/rss.png" alt="" />'+
					'<h2>'+ item["art_title"] +'</h2>'+
				'</a>'+
			'</div>'
			+ '<br>'+
			'<img class="art_img" src="'+ item["art_urlpicture"]+ '" alt="" />'
			+ 
			'<div class="art_main">'+
				'<a href="'+ item["art_urlsource"]+ '" >'+'<p class="art_newsportal">'+ item["art_newportal"] +'</p>'+'</a>'+
				'<p class="teaser">'+ item["art_teaser"] +'</p>'+
			'</div>'
			+ 
			'<div class="art_time">'+'<p>'+ item["art_date"]+ '</p>'+'</div>'+ '<br>'+

			'<div style="clear:both;">'+'</div>';
			

		$("#rightArticle").append("<div>" + oldArticle + "</div>");
		

		}); 
	});
}

function refresh(){ 
	$.getJSON("/start-search", function (data){ 
		if (data.new_art_count != 0){
			init();
		}

		
		}); 
	}
