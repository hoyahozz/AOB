<?php 
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

    $id = $_GET["id"];

    $statement = mysqli_prepare($con, "SELECT id FROM users WHERE id = ? ");
    mysqli_stmt_bind_param($statement, "s", $id);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $id);

    $response = array();
    $response["success"] = false;
    
    while(mysqli_stmt_fetch($statement)){
        $response["success"]=true;
    }
   
    echo json_encode($response);
?>