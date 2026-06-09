$url = "https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-sdk.zip"
$tempZip = "c:\Users\Anik\OneDrive\Documents\Java Project Final Lab\javafx-temp.zip"
$destFolder = "c:\Users\Anik\OneDrive\Documents\Java Project Final Lab\AlumniNetworkingSystem\lib"

if (Test-Path $tempZip) {
    Remove-Item $tempZip -Force -ErrorAction SilentlyContinue
}

Write-Output "Downloading JavaFX using curl to $tempZip ..."
curl.exe -L -o "$tempZip" $url

if (Test-Path $tempZip) {
    $size = (Get-Item $tempZip).Length
    Write-Output "Downloaded Zip size: $size bytes"
    if ($size -gt 40000000) {
        Write-Output "Extracting archive using Expand-Archive..."
        Expand-Archive -Path $tempZip -DestinationPath $destFolder -Force
        Write-Output "Extraction complete. Cleaning up..."
        Remove-Item $tempZip -Force
        Write-Output "Success!"
    } else {
        Write-Error "File is too small ($size bytes). Download failed."
    }
} else {
    Write-Error "Zip file not found after download."
}
