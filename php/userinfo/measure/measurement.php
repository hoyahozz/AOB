<?php
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

  $requestMethod = $_SERVER["REQUEST_METHOD"];


  switch($requestMethod) {
		case 'GET' : // GET 방식, 주행기록 출력
			$id = $_GET["id"];
			
			$statement = mysqli_prepare($con, "select * from measurement where id = ?");
			mysqli_stmt_bind_param($statement, "s", $id);
			mysqli_stmt_execute($statement);

			mysqli_stmt_store_result($statement);
			mysqli_stmt_bind_result($statement, $mnum, $id, $image, $time, $start_time, $end_time, $avg_speed, $dist, $kcal);

			$response = array();
			$response["success"] = false;
			$result = array();

			while(mysqli_stmt_fetch($statement)){
			$image = base64_encode($image);
			$response["success"] = true;
			$response["mnum"] = $mnum;
			$response["id"] = $id;
			$response["image"] = $image;
			$response["time"] = $time;
			$response["start_time"] = $start_time;
			$response["end_time"] = $end_time;
			$response["avg_speed"] = $avg_speed;
			$response["dist"] = $dist;
			$response["kcal"] = $kcal;
			array_push($result, array(
				"success"=>$response["success"], 
				"mnum" => $response["mnum"],
				"id" => $response["id"],
				"image" => $response["image"],
				"time" => $response["time"],
				"start_time" => $response["start_time"],
				"end_time" => $response["end_time"],
				"avg_speed" => $response["avg_speed"],
				"dist" => $response["dist"],  
				"kcal" => $response["kcal"]));
			}
			echo json_encode($result);
			break;
		case 'POST' : // POST 방식, 주행기록 추가
			$id = $_POST["id"];
			$image = $_POST["image"];
			$time = $_POST["time"];
			$dist = $_POST["dist"];
			$kcal = $_POST["kcal"];
			$avg_speed = $_POST["avg_speed"];
			$start_time = $_POST["start_time"];
			$end_time = $_POST["end_time"];
			
			$image = base64_decode($image);
			
				
			$statement = mysqli_prepare($con, "INSERT INTO measurement(id,image, time, start_time, end_time, avg_speed, dist,kcal) VALUES (?,?,?,?,?,?,?,?)");	
			mysqli_stmt_bind_param($statement, "ssissddd", $id, $image, $time, $start_time, $end_time, $avg_speed, $dist, $kcal);
			mysqli_stmt_execute($statement);
				
			$response = array();
			$response["success"] = true;
			echo json_encode($response);
			break;
				
		case 'DELETE' : // DELETE 방식, 주행기록 삭제
			$id = $_GET["id"];
			$fnum = $_GET["fnum"];
			
			$statement = mysqli_prepare($con, "DELETE FROM measurement WHERE id = ? and mnum = ?");	
			mysqli_stmt_bind_param($statement, "si", $id, $mnum);
			mysqli_stmt_execute($statement);
				
			$response = array();
			$response["success"] = true;
			echo json_encode($response);
			break;
			
		
		default : 
			break;
  }
  mysqli_close($con);
?>
