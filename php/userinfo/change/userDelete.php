<?php
    $con = mysqli_connect("IP", "ID", "PW", "SCHEMA");
    mysqli_query($con,'SET NAMES utf8');

    $id = $_POST["id"];
    $statement = mysqli_prepare($con, "DELETE FROM favorite WHERE id = ?");
    mysqli_stmt_bind_param($statement, "s", $id);
    mysqli_stmt_execute($statement);

    $statement = mysqli_prepare($con, "DELETE FROM measurement WHERE id = ?");
    mysqli_stmt_bind_param($statement, "s", $id);
    mysqli_stmt_execute($statement);

    $statement = mysqli_prepare($con, "DELETE FROM user WHERE id = ?");
    mysqli_stmt_bind_param($statement, "s", $id);
    mysqli_stmt_execute($statement);

    $response = array();
    $response["success"] = true;
    echo json_encode($response);
?>

