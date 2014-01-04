startSearch(1);

/*Initialisieren der aktuellen Artikel*/
function newArticles(offset){ 
	if(offset==0){ $("#article_list").html(""); }
	$("#main .btn_newArticle span").html("... Weitere Artikel laden ...");
	$("#main .btn_newArticle").removeClass("void");

	$.getJSON("/new-topics?offset="+offset, function (data){ 
		$.each(data.articles, function(i,item) { 

			var curArticle = " ";
			
			curArticle =				
				'<div class="art_header">'+
					'<a href="'+ item["art_urlsource"]+ '" target="_new">' +
						'<img class="img_rss" src="/assets/images/rss.png" alt="rss" />'+
						'<h2>'+ item["art_title"] +'</h2>'+
					'</a>'+
				'</div>'
				+ 
				'<img class="art_img" src="'+ item["art_urlpicture"] + '" alt="" />'
				+ 
				'<div class="art_main">'+
					'<a href="'+ item["art_urlsource"]+ '" target="_new">'+'<p class="art_newsportal">'+ item["art_newportal"] +'</p>'+'</a>'+
					'<p class="teaser">'+ item["art_teaser"] +'</p>'+
				'</div>'
				+ 
				'<div class="art_time">'+'<p><img class="calendar" src="/assets/images/calendar.png"/>'+ item["art_date"]+ '</p>'+'</div>'
				+
				'<div style="clear:both;">'+'</div>'
				+
				'<a href="#" onclick="similarArticle(\''+ item["art_topichash"]+'\')">'
				+
				'<div class="shows_similar">'
				+
				'<img src="/assets/images/plus.png"/>Ähnliche Artikel</div></a>'
				+
				'<div id="'+ item["art_topichash"]+'"><ul></ul></div>'+'<div style="clear:both;">';
			
			$("#article_list").append("<article>" + curArticle + "</article>");

			}); 
		
		if (data.articles.length == 0) {
			$("#main .btn_newArticle span").html("Keine weiteren Artikel vorhanden.");
			$("#main .btn_newArticle").addClass("void");
		}
	}); 
	
}


/*Anzeige Alte Artikel*/
function oldArticles(offset){
	if (offset==0) $("#article_list_old").html("");
	$("#old_news .btn_newArticle span").html("... Weitere Artikel laden ...");
	$("#old_news .btn_newArticle").removeClass("void");

	$.getJSON("/old-topics?offset="+offset, function (data){ 
		$.each(data.articles, function(i,item) { 
		
			var oldArticle = " ";
			if (i < data.articles.length && (i!=0 || offset==1)) oldArticle = '<hr />';		
			
			oldArticle += 		
				'<div class="old_art">'
				+
					'<a href="'+ item["art_urlsource"]+ '" target="_new">'
					+
					'<img class="img_rss" src="/assets/images/rss.png" alt="" />'
					+
					'<h2>'+ item["art_title"] +'</h2>'
					+
					'</a>'
					+
					'<p class="old_portal">'+ item["art_newportal"] +'</p>'
					+
					'<p class="old_date">'+ item["art_date"]+ '<p>'
					+
					'<div style="clear:both;"></div>'
					+
				'</div>';				

			$("#article_list_old").append(oldArticle);
		}); 
		
		if (data.articles.length == 0) {
			$("#old_news .btn_newArticle span").html("Keine weiteren Artikel.");
			$("#old_news .btn_newArticle").addClass("void");
		}
	});
}


/*Ähnliche Artikel*/
function similarArticle(topichash){
	$.getJSON("/similar-articles?topichash="+topichash, function (data){ 		
		$.each(data.articles, function(i,item) { 		
			
			var simArticle = " ";
			
			simArticle = 
				'<li>'
				+
				'<a href="'+ item["art_urlsource"]+ '" target="_new">'
				+
				item["art_title"]+'<br>'+item["art_date"]
				+
				'</a>'
				+
				'</li>';
			
			$("#"+topichash+" ul").append(simArticle);			
			}); 
		});	
}


/*Anfrage nach neuen Artikeln*/
function startSearch(i){
	$("#btn_refresh").addClass("rotate");
	$.getJSON("/start-search", function (data){ 
		$("#btn_refresh").removeClass("rotate");
		
		if (data.new_art_count != 0 || i==1){
			newArticles(0);
			oldArticles(0);
			portals();
		}	
	}); 
	
	$("#head_wrapper").css("display","true");	
}


/*Statistik*/
function portals(){
	$.getJSON("/news-portals", function (data){ 
		$.each(data.newsportals, function(i,item) { 
			//alert("test");
			var portals = " ";
					
			portals =				
			'<table id="bar">'
				+
			    '<tbody>'
				+
			        '<tr class="bar"><th>' + item["np_name"] 
				+
			        '</th><td><div style="width:' + item["np_count"] +'px;">&nbsp;</div>' 
			        + item["np_count"] +'</td></tr>'
			    +
			    '</tbody>'
			    +
			'</table>';			
			
			$("#portals").append(portals);
		}); 
	});
}