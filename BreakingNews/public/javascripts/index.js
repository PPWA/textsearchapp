init();
refresh();
portals();

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
				'<img class="art_img" src="'+ item["art_urlpicture"] + '" alt="" />'
				+ 
				'<div class="art_main">'+
					'<a href="'+ item["art_urlsource"]+ '" >'+'<p class="art_newsportal">'+ item["art_newportal"] +'</p>'+'</a>'+
					'<p class="teaser">'+ item["art_teaser"] +'</p>'+
				'</div>'
				+ 
				'<div class="art_time">'+'<p>'+ item["art_date"]+ '</p>'+'</div>'
				+
				'<div style="clear:both;">'+'</div>';
			
			$("#main").append("<article>" + curArticle + "</article>");

			}); 
	}); 
	oldArticles();
}


function oldArticles(){
$.getJSON("/old-topics", function (data){ 
	$.each(data.articles, function(i,item) { 
	
		var oldArticle = " ";
		
		oldArticle = 
		
			'<div class="old_art">'+
				'<a href="'+ item["art_urlsource"]+ '">'+
				'<img class="img_rss" src="/assets/images/rss.png" alt="" />'+
				'<h2>'+ item["art_title"] +'</h2>'+
				'</a>'+
				'<p class="old_portal">'+ item["art_newportal"] +'</p>'+
				'<p class="old_date">'+ item["art_date"]+ '<p>'+
				'<div style="clear:both;"></div>'+
			'</div>'+
			'<hr />';		

		$("#old_news").append(oldArticle);
		

		}); 
	});
}

/*anfrage nach neuen Artikeln*/
function refresh(){
	$.getJSON("/start-search", function (data){ 
		if (data.new_art_count != 0){
			init();
		}
		
	}); 
	
}



/*portals*/
function portals(){
	$.getJSON("/news-portals", function (data){ 
		$.each(data.newsportals, function(i,item) { 
			//alert("test");
			var portals = " ";
			var sum="";
		
		
			portals = 
				
				'<div class="dia_name">' + item["np_name"] +'</div>' 
				+
				'<div class="dia_anzahl">' + item["np_count"] +'</div>'
				+
				'<br>';

				

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

