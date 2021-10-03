<?php
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

    $id= $_POST["id"];
    $pw = $_POST["pw"];
    $image = $_POST["image"];
    $name = $_POST["name"];
    $number = $_POST["number"];
    $email = $_POST["email"];
    $sos = $_POST["sos"];

    $statement = mysqli_prepare($con, "INSERT INTO user(id,pw,image,name,number,email,sos) VALUES (?,?,?,?,?,?,?)");
    mysqli_stmt_bind_param($statement, "ssissss", $id, $pw, $image, $name, $number, $email, $sos);
    mysqli_stmt_execute($statement);

    $response = array();
    $response["success"] = true;

    echo json_encode($response);

?>