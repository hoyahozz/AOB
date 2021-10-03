<?php
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

    $id = $_GET["id"];
    $pw = $_GET["pw"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE id = ? AND pw = ?");
    mysqli_stmt_bind_param($statement, "ss", $id, $pw);
    mysqli_stmt_execute($statement);

    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $id, $pw, $image, $name, $number, $email, $sos);
    $response = array();
    $response["success"] = false;

    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["id"] = $id;
        $response["pw"] = $pw;
        $response["image"] = $image;
        $response["name"] = $name;
        $response["number"] = $number;
        $response["email"] = $email;
        $response["sos"] = $sos;
    }
    echo json_encode($response);
?>