startSearch(1);
portals();

/*Initialisieren der aktuellen Artikel*/
function newArticles(i){ 
	if(i==0){ $("#article_list").html(""); }
		
	$.getJSON("/new-topics?offset="+i, function (data){ 
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
				'<div class="art_time">'+'<p>'+ item["art_date"]+ '</p>'+'</div>'
				+
				'<div style="clear:both;">'+'</div>';
			
			
			$("#article_list").append("<article>" + curArticle + "</article>");

			}); 
		if(data.articles.length == 0){
			$("#more_articles span").html("Keine weiteren Artikel");
			$("#more_articles").css("background-color", "red");
		}
	}); 
	
}

/*Anzeige Alte Artikel*/
function oldArticles(){
$.getJSON("/old-topics", function (data){ 
	$.each(data.articles, function(i,item) { 
	
		var oldArticle = " ";
		
		oldArticle = 		
			'<div class="old_art">'+
				'<a href="'+ item["art_urlsource"]+ '" target="_new">'+
				'<img class="img_rss" src="/assets/images/rss.png" alt="" />'+
				'<h2>'+ item["art_title"] +'</h2>'+
				'</a>'+
				'<p class="old_portal">'+ item["art_newportal"] +'</p>'+
				'<p class="old_date">'+ item["art_date"]+ '<p>'+
				'<div style="clear:both;"></div>'+
			'</div>';
		
		if (i < data.articles.length-1){
			oldArticle +='<hr />';
		}
					

		$("#old_news").append(oldArticle);

		}); 
	});
}

/*Ähnliche Artikel*/
function similar(){
	$.getJSON("/similar-articles", function (data){ 
		$.each(data.articles, function(i,item) { 
		
			var simArticle = " ";
			
			simArticle = 
				'<div class="art_similar">'+
				'<a href="" target="_new">'+
				'<p>'+'<img src="/assets/images/plus.png" alt="" />'+ '</p>'+'</a>'+'</div>';		
			
			alert("test");
			
			$("#article_list").append("<article>" + simArticle + "</article>");
			

			}); 
		});	
}

/*Anfrage nach neuen Artikeln*/
function startSearch(i){
	$.getJSON("/start-search", function (data){ 
		if (data.new_art_count != 0 || i==1){
			newArticles(0);
			oldArticles();
		}
		
	}); 
	
}



/*Statistik*/
function portals(){
	$.getJSON("/news-portals", function (data){ 
		$.each(data.newsportals, function(i,item) { 
			//alert("test");
			var portals = " ";
					
			portals =				
			'<table id="bar">'+
			    '<tbody>'+
			        '<tr class="bar"><th>' + item["np_name"] +
			        '</th><td><div style="width:' + item["np_count"] +'px;">&nbsp;</div>' + item["np_count"] +'</td></tr>'+
			    '</tbody>'
			+
			'</table>';
			
			
			$("#portals").append(portals);

		}); 
	});
}



/*Laden des WaitImages*/

var img = "/assets/images/waitBird.png";

function waitImage(){
	 spriteImage = document.getElementById("btn_refresh");
	 spriteImage.src = img;
	 alert("Neue Artikel geladen");
}

