/*
 * creates a new XMLHttpRequest object which is the backbone of AJAX,
 * or returns false if the browser doesn't support it
 */
function getXMLHttpRequest() {
	var xmlHttpReq = false;
	// to create XMLHttpRequest object in non-Microsoft browsers
	if (window.XMLHttpRequest) {
		xmlHttpReq = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {
			// to create XMLHttpRequest object in later versions
			// of Internet Explorer
			xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (exp1) {
			try {
				// to create XMLHttpRequest object in older versions
				// of Internet Explorer
				xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (exp2) {
				xmlHttpReq = false;
			}
		}
	}
	return xmlHttpReq;
}

/*
 * AJAX call starts with this function
 */
function getMapDataFromServer(bounds, begin, end) {
	var xmlHttpRequest = getXMLHttpRequest();
	xmlHttpRequest.open("POST", "MapLoader", false);
	xmlHttpRequest.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded");

	var parameter = bounds.getNorthEast().lat().toString() + ",";
	parameter += bounds.getNorthEast().lng().toString() + ";";
	parameter += bounds.getSouthWest().lat().toString() + ",";
	parameter += bounds.getSouthWest().lng().toString() + ";";
	//parameter += toUTC(begin) + ";" + toUTC(end);
	parameter +=begin+ ";" + end;
	xmlHttpRequest.send(parameter);
	return xmlHttpRequest.responseText;
}

var isRealtime = true;
var timer;
var interval = 5000;
var realtimeBegin;
var begin;
var end;

var heatmap;
var map;
var loc;

function init() {
	log("init...");
	startTimer();
	log("");
}

function initMap() {
	log("init map...")
	map = new google.maps.Map(document.getElementById('map-canvas'), {
		center : new google.maps.LatLng(0, 0),
		zoom : 2,
	});
	log("");
}

function submit() {
	log("submit...");
	if (loc == null || loc != $("#locinput").val()) {
		loc = $("#locinput").val();
		log("location: " + loc);
		updateCenter();
	} else if (!isRealtime) {
		updateOneTime();
	}
	log("");
}

function updateCenter() {
	if (loc == "") {
		log("update center: 0, 0");
		map.setZoom(2);
		map.setCenter(new google.maps.LatLng(0, 0));
		if (!isRealtime)
			updateOneTime();
		return;
	}

	log("update center...");
	var geocoder = new google.maps.Geocoder();
	geocoder.geocode({
		'address' : loc
	}, function(results, status) {
		log("get location from google.");
		if (status == google.maps.GeocoderStatus.OK) {
			map.setZoom(5);
			map.setCenter(results[0].geometry.location);

			if (!isRealtime)
				updateOneTime();
		} else {
			alert("Could not find location: " + location);
		}
	});
}

function updateOneTime() {
	log("update map one time. ");
	begin = $("#beginPicker").val();
	if ($("#endPicker").val() == "")
		end = currentDatetime();
	else
		end = $("#endPicker").val();
	log("begin time: " + begin);
	log("end time: " + end);

	if (heatmap != null)
		heatmap.setMap(null);
	updateHeatmap(begin, end);
	log("");
}

function updateRealTime() {
	log("update map real time.");
	if (begin == null || realtimeBegin != $("#beginPicker").val())
		begin = realtimeBegin = $("#beginPicker").val();
	else
		begin = end;
	end = currentDatetime();
	log("begin time: " + begin);
	log("end time: " + end);

	updateHeatmap(begin, end);
	log("");
}

function updateHeatmap(begin, end) {
	log("try to get data from server...");
	var mapdata = getMapDataFromServer(map.getBounds(), begin, end);
	var places = mapdata.split(";");
	var heatmapdata = [];
	for (var i = 0; i < places.length; ++i) {
		if (places[i] == "")
			continue;
		var latlng = places[i].split(",");
		log("DATA "+i+" : "+latlng[0]+"  "+latlng[1]);
		heatmapdata[i] = new google.maps.LatLng(latlng[0], latlng[1]);
	}

	log("get " + heatmapdata.length + " locations.");
	if (heatmapdata.length == 0)
		return;

	log("update heat map.");
	heatmap = new google.maps.visualization.HeatmapLayer({
		data : heatmapdata
	});
	heatmap.setMap(map);
}

function currentDatetime() {
	return toDatetimeString(new Date());
}

function toDatetimeString(date) {
	str = date.getFullYear() + "-" + (date.getMonth() + 1) + "-"
			+ date.getDate();
	str += " " + date.getHours() + ":" + (date.getMinutes()) + ":"
			+ date.getSeconds();
	return str;
}

function toUTC(datetime) {
	var datetimeSet = datetime.split(" ");
	var dateSet = datetimeSet[0].split("-");
	var timeSet = datetimeSet[1].split(":");

	var d = new Date(
			Number(dateSet[0]), 	// year
			Number(dateSet[1]) - 1, // month
			Number(dateSet[2]),		// date
			Number(timeSet[0]), 	// hour
			Number(timeSet[1]),		// minute
			Number(timeSet[2])		// second
	);

	var utc = d.getTime() + (d.getTimezoneOffset() * 60000);
	var date = new Date(utc - 60 * 5);
	return toDatetimeString(date);
}

function startTimer() {
	log("start timer..");
	timer = setInterval(function() {
		updateRealTime();
	}, interval);
}

function stopTimer() {
	log("stop timers.");
	clearInterval(timer);
}

function switchRealtime() {
	$("#endPicker").val("");
	if (heatmap != null)
		heatmap.setMap(null);
	begin = end = null;

	if ($("#realtime").is(":checked")) {
		$("#endPicker").attr("disabled", "disabled");
		startTimer();
		isRealtime = true;
	} else {
		$("#endPicker").removeAttr("disabled");
		stopTimer();
		isRealtime = false;
	}
}

$(function() {
    $("#locinput").keypress(function (e) {
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            return false;
        } else {
            return true;
        }
    });
});

function log(msg) {
	txt = $("#status").val();
	txt += msg + "\n";
	$("#status").val(txt);
	$("#status").scrollTop($("#status")[0].scrollHeight);
}


function getSentiment(){
    $.getJSON("sentiment", {time:begin },function(data){
        //data format:
        //{outcome: [{sid:sid, polarity: "negative"|"positive", score: score}, ...]}
       var evals = data.outcome;
       $.each(evals, function(index, value){
            $("#status").val(value.sid +": "+value.polarity +" "+value.score );
            $("#status").scrollTop($("#status")[0].scrollHeight);
       });
    });

}

//run getSentiment periodically
setInterval(function() {
    getSentiment(); 
}, interval);
