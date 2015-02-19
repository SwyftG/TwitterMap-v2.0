<!DOCTYPE html>

<html>
<head>
<title>TwittMap</title>


<link rel="stylesheet" type="text/css" href="styles/styles.css" />
<link rel="stylesheet" type="text/css" href="styles/anytime.5.0.5.css" />
<script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="js/anytime.5.0.5.js"></script>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?libraries=visualization&sensor=true_or_false">
</script>
<script type="text/javascript" src="js/ajax.js"></script>
<script type="text/javascript">
	google.maps.event.addDomListener(window, 'load', initMap);
</script>

</head>
<body>
	<div id="Config">
		<input type="checkbox" id="realtime" checked
			onclick="switchRealtime()">Real-Time <input type="text"
			placeholder="The begin time ..." id="beginPicker" /> <input
			type="text" placeholder="The end time .." id="endPicker" disabled />
		<script>
			AnyTime.picker('beginPicker');
			AnyTime.picker('endPicker');
			$("#beginPicker").val(currentDatetime());
		</script>

		<input type="text" id="locinput"
			placeholder="The city you care about ..." style="width: 30%" />
		<button onclick="submit()" style="width: 10%">submit</button>
	</div>
	<div id="Main" style="width: 100%; height: 100%">
		<div id="map-canvas" style="width: 80%; float: left; height: 600px"></div>
		<div style="width: 19%; height: 100%; float: left;">
		<textarea id="status" readonly style="width: 838px; height: 303px"></textarea>
		</div>
	</div>
	<script>
		init();
	</script>
</body>
</html>