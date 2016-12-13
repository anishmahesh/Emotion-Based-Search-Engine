//jQuery to collapse the navbar on scroll
$(window).scroll(function() {
    if ($(".navbar").offset().top > 50) {
        $(".navbar-fixed-top").addClass("top-nav-collapse");
    } else {
        $(".navbar-fixed-top").removeClass("top-nav-collapse");
    }
});

//jQuery for page scrolling feature - requires jQuery Easing plugin
$(function() {
  /*
    $('a.page-scroll').bind('click', function(event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top
        }, 1500, 'easeInOutExpo');
        event.preventDefault();
    });
   */
});


$( document ).ready(function() {
        var url      = window.location.href;     // Returns full URL
        
        var getUrlParameter = function getUrlParameter(sParam) {
                    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
                    sURLVariables = sPageURL.split('&'),
                    sParameterName,
                    i;
                    
                    for (i = 0; i < sURLVariables.length; i++) {
                        sParameterName = sURLVariables[i].split('=');
                    
                        if (sParameterName[0] === sParam) {
                            return sParameterName[1] === undefined ? "" : sParameterName[1];
                        }
                    }
                    return "";
        };
        
        function submitValidation(){
            if ($("#searchBar").val(query) == ""){
                    alert("Oh Ohh! Please enter a query to continue.");
                    return false;
            }
            return true;
        }
                    
        var query = decodeURI(getUrlParameter("query")).replace(/\+/g, ' ');
        var num = getUrlParameter("num");
        var fromIndex = getUrlParameter("fromIndex");
        var emo = getUrlParameter("emotion");
        var pagination = getUrlParameter("pagination");

        $("#format").val("html");
        if (num == ""){
            $("#num").val("10");
        }else{
            $("#num").val(num);
        }

	if(pagination == ""){
	    $("#pagination").val("1");
	}else{
	    if(parseInt(pagination)>1)
	        document.getElementById("prev-page").style.display = "inline-block";
            $("#pagination").val(parseInt(pagination)+1);
	}
                    
        if (query == ""){
            $("#searchBar").val("");
        }else{
	    $("#searchKey").val(query);
            $("#searchBar").val(query);
        }
                    
                    
        if(emo == ""){
            $("#emotion").val("funny");
        }else{
                if(emo == "joy"){
                    document.getElementById("btn-emo").src="static/images/happy.png";
                }else if(emo == "sad"){
                    document.getElementById("btn-emo").src="static/images/sad.png";
                }
                $("#emotion").val(emo);
        }

        $("#submitSearch").click(function() {
	    $("#pagination").val("1");
            $("#searchKey").val($("#searchBar").val());
            $( "#searchForm" ).submit();
        });

	$("#next-page").click(function() {
	    $( "#searchForm" ).submit();
	});

	$("#prev-page").click(function() {
	    window.history.back();
	});
                    
        $( "#emo-funny" ).click(function() {
            $("#emotion").val("funny");
            document.getElementById("btn-emo").src="static/images/funny.png";
        });
                    
        $( "#emo-happy" ).click(function() {
            $("#emotion").val("joy");
            document.getElementById("btn-emo").src="static/images/happy.png";
        });
                    
        $( "#emo-sad" ).click(function() {
            $("#emotion").val("sad");
            document.getElementById("btn-emo").src="static/images/sad.png";
        });
                    
        $("#searchBar").keypress(function (e) {
            if (e.which == 13) {
                    $("#searchKey").val($("#searchBar").val());
                    $('#searchForm').submit();
                    return false;
            }
        });
                    $("#searchBar").autocomplete(
                                          {
                                          search: function () {},
                                          source: function (request, response)
                                          {
                                          $.ajax(
                                                 {
                                                 url: "/fill?term="+$("#searchBar").val(),
                                                 dataType: "json",
                                                 success: function (data)
                                                 {
                                                    console.log(data);
                                                    console.log(data.autofill);
                                                    response(data.autofill);
                                                 }
                                                 });
                                          },
                                          minLength: 1,
                                          select: function (event, ui)
                                          {
                                                 var test = ui.item ? ui.item.id : 0;
                                                 if (test > 0)
                                                 {
                                                    alert(test);
                                                 }
                                          }
                    });
});
