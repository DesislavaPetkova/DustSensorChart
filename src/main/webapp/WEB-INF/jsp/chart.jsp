<!-- chart.jsp-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style><%@include file="../css/tools.css"%></style>
<%--<link rel="stylesheet" href="../css/tools.css" >--%>
<header class="header">
    <div class="container">
        <h1 class="site-title">Sensor Chart View</h1>

        <span class="site-tagline">Because flexbox is super cool!</span>
    </div>
</header>
<nav class="main-nav">
    <div class="container">
        <ul>
            <li class="mobile-button"><a href="#">Menu</a></li>
            <li><a href="#">Sensor view</a></li>
            <li><a href="#">Work</a></li>
        </ul>
    </div>
</nav>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <script type="text/javascript">
        window.onload = function() {

            var dps = [[]];
            var chart = new CanvasJS.Chart("chartContainer", {
                theme: "light2", // "light1", "dark1", "dark2"
                animationEnabled: true,
                title: {
                    text: "Diamond Production - Canada"
                },
                subtitles: [{
                    text: "2006 - 2016"
                }],
                axisX: {
                    valueFormatString: "####"
                },
                axisY: {
                    title: "Volume (in million carats)"
                },
                data: [{
                    type: "spline",
                    xValueFormatString: "####",
                    yValueFormatString: "#,##0.0 million carats",
                    dataPoints: dps[0]
                }]
            });

            var xValue;
            var yValue;

            <c:forEach items="${dataPointsList}" var="dataPoints" varStatus="loop">
            <c:forEach items="${dataPoints}" var="dataPoint">
            xValue = parseInt("${dataPoint.x}");
            yValue = parseFloat("${dataPoint.y}");
            dps[parseInt("${loop.index}")].push({
                x : xValue,
                y : yValue
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
</body>
<section class="content">
    <div class="full-bleed cool-photo"></div>
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

</section>
<footer class="footer">
    <div class="container">
        <p>Dust sensor chart view </p>
    </div>
</footer>
</html>