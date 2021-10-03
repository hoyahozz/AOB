<?php
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

    $id = $_POST["id"];
    $image = $_POST["image"];
    $name = $_POST["name"];
    $number = $_POST["number"];
    $email = $_POST["email"];
    $sos = $_POST["sos"];

  $statement = mysqli_prepare($con, "UPDATE user SET image = ? , name = ? , number = ? , email = ? , sos = ? WHERE id = ?");
  mysqli_stmt_bind_param($statement, "isssss", $image, $name, $number, $email, $sos, $id);
  mysqli_stmt_execute($statement);

  $response = array();
  $response["success"] = true;

  echo json_encode($response);
?>
