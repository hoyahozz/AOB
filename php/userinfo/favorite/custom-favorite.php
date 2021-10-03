<?php
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

  $requestMethod = $_SERVER["REQUEST_METHOD"];


  switch($requestMethod) {
		case 'GET' : // GET 방식, 목록 출력
			$id = $_GET["id"];
			
			$statement = mysqli_prepare($con, "select * from favorite where id = ?");
			mysqli_stmt_bind_param($statement, "s", $id);
			mysqli_stmt_execute($statement);

			mysqli_stmt_store_result($statement);
			mysqli_stmt_bind_result($statement, $fnum, $id, $latitude, $longitude, $title, $content);

			$response = array();
			$response["success"] = false;
			$result = array();

			while(mysqli_stmt_fetch($statement)){
			$response["success"] = true;
			$response["fnum"] = $fnum;
			$response["id"] = $id;
			$response["latitude"] = $latitude;
			$response["longitude"] = $longitude;
			$response["title"] = $title;
			$response["content"] = $content;
			array_push($result, array(
			"success"=>$response["success"], 
			"fnum" => $response["fnum"],
			"id" => $response["id"],
			"latitude" => $response["latitude"],
			"longitude" => $response["longitude"],  
			"title" => $response["title"],
			"content" => $response["content"]));
			}

			echo json_encode($result);
			break;
		case 'POST' : // POST 방식, 즐겨찾기 목록 추가
			$id = $_POST["id"];
			$latitude = $_POST["latitude"];
			$longitude = $_POST["longitude"];
			$title = $_POST["title"];
			$content = $_POST["content"];
				
				
			$statement = mysqli_prepare($con, "INSERT INTO favorite(id,latitude,longitude,title,content) VALUES (?,?,?,?,?)");	
			mysqli_stmt_bind_param($statement, "sddss", $id, $latitude, $longitude, $title, $content);
			mysqli_stmt_execute($statement);
				
			$response = array();
			$response["success"] = true;
			echo json_encode($response);
			break;
				
		case 'DELETE' : // DELETE 방식, 즐겨찾기 목록 삭제
			$id = $_GET["id"];
			$fnum = $_GET["fnum"];
			
			$statement = mysqli_prepare($con, "DELETE FROM favorite WHERE id = ? and fnum = ?");	
			mysqli_stmt_bind_param($statement, "si", $id, $fnum);
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
