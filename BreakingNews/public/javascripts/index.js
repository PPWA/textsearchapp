var DELTA_RELOAD_CLIENT = 1*60*1000;
var PORTALS_COUNT = 5;
startSearch(1);
autoReload();


/*Prüft periodisch, ob Lucene-Index neue Artikel enthält*/
function autoReload() {
   setInterval(function(){startSearch(0)},DELTA_RELOAD_CLIENT);
}


/*Initialisieren der aktuellen Artikel*/
function newArticles(offset,phrase){
        if(offset==0){ $("#article_list").html(""); $("#main .btn_newArticle").hide(); }
        $("#main .btn_newArticle span").html("... Weitere Artikel laden ...");
        $("#main .btn_newArticle").removeClass("void");
        

        $("#btn_refresh").addClass("rotate");
        $.getJSON("/new-topics?offset="+offset+"&keyword="+phrase, function (data){
                $("#btn_refresh").removeClass("rotate");
                $.each(data.articles, function(i,item) {

                        var curArticle = " ";
                        
                        curArticle =                                
                                '<div class="art_header">'+
                                        '<a href="#" onclick="loadIframe(\''+ item["art_urlsource"]+ '\');return false;">' +
                                                '<img class="img_rss" src="/assets/images/rss.png" alt="rss" />'+
                                                '<h2>'+ item["art_title"] +'</h2>'+
                                        '</a>'+
                                '</div>'
                                +
                                '<img class="art_img" src="'+ item["art_urlpicture"] + '" alt="" />'
                                +
                                '<div class="art_main">'+
                                        '<a href="#" onclick="loadIframe(\''+ item["art_urlsource"]+ '\');return false;">'+'<p class="art_newsportal"><img class="np" src="/assets/images/blog.gif"/>'+ item["art_newportal"] +'</p>'+'</a>'+
                                        '<p class="teaser">'+ item["art_teaser"] +'</p>'+
                                '</div>'
                                + '<div id="button" class="shows_similar"><a href="#" onclick="posSimOverlay(event);similarArticles(\''+ item["art_topichash"]+'\');return false;">'+
                                '<img src="/assets/images/plus.png"/>Ähnliche Artikel</a></div>'+
                                '<div class="art_time">'+'<p><img class="calendar" src="/assets/images/calendar.png"/>'+ item["art_date"]+ '</p>'+'</div>'
                                +
                                '<div style="clear:both;">'+'</div>'+
                                
                                '<div style="clear:both;">';
                                
                                $("#article_list").append("<article style=\"display:none\">" + curArticle + "</article>");
                                //$("article").show( 'blind', {}, 550);
                                $("article").fadeIn(750,function(){$("#main .btn_newArticle").show();});

                        });
                if (data.articles.length == 0) {
                        $("#main .btn_newArticle").show();
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
                                '<div style="display:none" class="old_art">'+
                                        '<a href="#" onclick="loadIframe(\''+ item["art_urlsource"]+ '\');return false;">'+
                                        '<img class="img_rss" src="/assets/images/rss.png" alt="" />'+
                                        '<h2>'+ item["art_title"] +'</h2>'+
                                        '</a>'+
                                        '<div class="old_portal">'+ item["art_newportal"] +'</div>'+
                                        '<div class="old_date">'+ item["art_date"]+ '<div>'+
                                        '<div style="clear:both;"></div>'+
                                '</div>';                                

                        $("#article_list_old").append(oldArticle);
                });
                if (data.articles.length == 0) {
                        $("#old_news .btn_newArticle span").html("Keine weiteren Artikel.");
                        $("#old_news .btn_newArticle").addClass("void");
                }
                                if (offset == 0) {
                        setTimeout(function(){$( ".old_art" ).show( 'slide', {duration:1500, easing:'easeOutElastic'})},550);
                }
                else {
                        $( ".old_art" ).fadeIn();
                }
        });
}

/*Ähnliche Artikel*/
function similarArticles(topichash){
        $("#similar_overlay ul").html("");
        $.getJSON("/similar-articles?topichash="+topichash, function (data){                 
                $.each(data.articles, function(i,item) {                 
                        
                        var simArticle = "";
                        
                        simArticle =
                                '<p><li>'+
                                '<a href="#" onclick="loadIframe(\''+ item["art_urlsource"]+ '\');return false;">'+
                                item["art_title"]+'<br></a><span>'+item["art_date"] +'</span>'
                                +'<br/><span>Grund für Ähnlichkeit: '+item["art_explanation"] +'</span>'+
                                '</li></p>';                        

                        $("#similar_overlay ul").append(simArticle);                        
                        //fadeIn;
                });
                if ($("#similar_overlay ul").html()=="") {
                        $("#similar_overlay ul").append("Keine ähnlichen Artikel vorhanden.");
                }
                $( "#similar_overlay" ).show( 'fold', {}, 500);
        });        
}

/*Anfrage nach neuen Artikeln*/
function startSearch(i){
        $("#btn_refresh").addClass("rotate");
        $.getJSON("/start-search", function (data){
                $("#btn_refresh").removeClass("rotate");
                if (data.new_art_count != 0 || i==1){
                        newArticles(0,'');
                        oldArticles(0);
                        portals();
                }        
        });
}

/*Statistik*/
function portals() {
        $('#portals').html("");
        $.getJSON("/news-portals", function (data){
                gesamt = 0;
                max = 0;
                $.each(data.newsportals, function(i,item) {
                        gesamt = gesamt + item["np_count"];
                        if(item["np_count"] > max) max = item["np_count"];
                });
                $.each(data.newsportals, function(i,item) {
                        if (i == PORTALS_COUNT) return false;
                        calcRelative = Math.round(100/max * item["np_count"]);
                        calcAbsolute = Math.round(100/gesamt * item["np_count"]);
                        addClass = '';
                        if (item["np_count"] == max) addClass = ' bestPortal';

                        titel = item["np_name"] + " (" + item["np_count"] +")";
                        portal = '<div title="' + titel + '" class="portal' + addClass + '" style="display:none;width:' + calcRelative + '%;">' +
                                 '<p style="display:none">' + titel + '</p>'+
                                 '</div><div style="display:none" class="prozent">' + calcAbsolute + '%</div><div style="clear:both"></div>';
                        $('#portals').append(portal);
                        setTimeout(function(){$(".portal").show( 'slide',{duration:1500, easing:'easeOutBounce'});$(".prozent").show( 'slide',{duration:1500, easing:'easeOutExpo'}, function(){$(".portal p").show();})},650);

                });
        });
}

$(function() {
    $(document).tooltip();
  });

function posSimOverlay(event) {
        $(".overlay").hide();
        $("#similar_overlay").css("top",event.pageY+40);
        $("#similar_overlay").css("left",event.pageX-70);
}

function loadIframe(url) {
        $("#extern_article").hide();
        $("iframe").attr("src",url);
        if(window.innerHeight > 700) {
                $("#extern_article").css("top",window.innerHeight/2-350+pageYOffset);
        }
        else {
                $("#extern_article").css("top",pageYOffset+55);        
        }
        $("#extern_article").css("left",window.innerWidth/2-500);
        $("#extern_article").show( 'fold', {}, 850);
}