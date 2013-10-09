$ ->
	$.get "/bars", (data) ->
		$.each data, (index, bar) ->
			$("#bars").append $("<li>").text bar.name