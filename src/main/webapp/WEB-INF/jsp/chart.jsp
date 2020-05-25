<!-- chart.jsp-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <link href="CSS/Master.css" rel="stylesheet" type="text/css">
    <!-- include the jQuery UI style sheet -->
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <!-- include jQuery -->
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <!-- include jQuery UI -->
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

    <script src="https://cdn.jsdelivr.net/momentjs/2.14.1/moment.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">

</head>
<style>
    <%@include file="../css/tools.css" %>
</style>
<header class="header">
    <div class="container">
        <h1 class="site-title">Статистика на данните</h1>
    </div>
</header>
<nav class="main-nav">
    <div class="container">
        <ul>
            <li class="mobile-button"><a href="#">Menu</a></li>
            <!-- <li><a href="#">Sensor view</a></li>
            <li><a href="#">Work</a></li> -->
        </ul>
    </div>
</nav>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <script type="text/javascript">
        window.onload = function () {

            var dps = [[]];
            var chart = new CanvasJS.Chart("chartContainer", {
                theme: "light2", // "light1", "dark1", "dark2"
                animationEnabled: true,
                zoomEnabled: true,
                    axisX: {
                    title: "Час и Дата",
                    xValueType: "line",
                    intervalType: "hour",
                    valueFormatString: "D'th' MMMM Y hh:mm tt"
                },
                axisY: {
                    title: " "
                },
                data: [{
                    type: "spline",
                    dataPoints: dps[0]
                }]
            });

            var xValue;
            var yValue;

            <c:forEach items="${dataPointsList}" var="dataPoints" varStatus="loop">
            <c:forEach items="${dataPoints}" var="dataPoint" >
            xValue = "${dataPoint.x}".split(/\D+/);
            yValue = parseFloat("${dataPoint.y}");
            dps[parseInt("${loop.index}")].push({
                x: new Date(xValue[0], xValue[1]-1, xValue[2], xValue[3], xValue[4], xValue[5]),
                y: yValue
            });
            </c:forEach>
            </c:forEach>

            chart.render();

        }

    </script>
</head>

<body>
<div id="chartContainer" style="height: 370px; width: 100%;"></div>
<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>

<div id="wrap">
	<table>
	<tr>
	<td  style="background: #E5989B; border: 1px solid black; padding: 30px; text-transform: uppercase;">
	<center>
        <font size="4px"><b> Филтриране на дата </font></b>
		<br><br><br>
        <form method="post" action="/chart" name="myForm" onsubmit="return validateDateForm()">
                <p>От дата: <input type='text' name="start" class="form-control" id="start"/></p>
                <p>До дата: <input type='text' name="end" class="form-control" id="end"/></p>
                <input type="submit" value="Филтрирай" class="mySub">
        </form>
	</center>
	</td>
	<td width="20px"></td>
	<td style="background: #E5989B; border: 1px solid black; padding: 30px; text-transform: uppercase;">
    <div id="sensor">
	<center>
        <font size="2px"><b>Настройки на сензора</b></font>
		<br><br>
		<table align="center">
		<tr>
			<td>
            <form method="post" action="/start" name="sensorStart">
                <input type="submit" value="Старт" class="myStart">
            </form>
			</td>
			<td width="30px"></td>
			<td>
            <form method="post" action="/stop" name="sensorStop">
                <input type="submit" value="Стоп" class="myStop">
            </form>
			</td>
		</tr>
		</table>
		<br>
        <div id="third">
            <form method="post" action="/timeout" name="filterDate" onsubmit="return validateDateTimeout()">
                <div class="timeout">
                    <font size="2px"><b>Продължителност на измерване</b></font> <br />
					<input type="text" name="timeout"><br />
					<br>
                    <input type="submit" value="Задаване" class="mySub"><br />
					<br>
                </div>
            </form>
             </form>
                <form method="post" action="/lastValue" name="getDens">
                    <font size="2px"><b>Моментна стойност</b></font> <br />
					<input type="text" name="lastValue" value="${lastDensReport}" readonly><br />
					<br>
					<input type="submit" value="Намери" class="mySub"><br />
             </form>
        </div>
	</center>
    </div>
	</td>
	</tr>
	</table>
</div>


<script type="text/javascript">

    $(function () {
        $('#start').datetimepicker({format: 'YYYY/MM/DD HH:mm'});
        $('#end').datetimepicker({format: 'YYYY/MM/DD HH:mm'});
    });

    function validateDateForm() {
        var x = document.forms["myForm"]["start"].value;
        var y = document.forms["myForm"]["end"].value;
        if (x == "" && y == "") {
            window.alert("Please enter valid date");
            return false;
        }
    }

    function validateDateTimeout() {

        var time = document.forms["timeoutForm"]["timeout"].value;

        if (time == "" || time < 0) {
            window.alert("Please enter valid timeout in seconds");
            return false;
        }

    }

</script>
</body>
<%--<section class="content">
    <article class="post">
        <div class="container">
            <h2>This is the super cool section title</h2>
            <div class="columns">
                <div class="item">
                    <h4 class="item-title">This is the post title</h4>

                    <p>Frankly, it's ludicrous to have these interlocking </p>
                </div>
            </div>
        </div>
    </article>

</section>--%>
<footer class="footer">
    <div class="container">
        <!-- <p>Dust sensor chart view </p> -->
    </div>
</footer>
</html>