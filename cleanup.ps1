#Specify the path to the Chromium folder relative to the script's location
$ChromiumFolderPath = Join-Path -Path $PSScriptRoot -ChildPath "chrome-win"

#Get all chrome.exe processes
$chromeProcesses = Get-Process chrome -ErrorAction SilentlyContinue

#Iterate over the chrome processes
foreach($process in $chromeProcesses)
{
    #Get the full path of the process executable
    $path = (Get-WmiObject -Query "Select * From Win32_Process Where ProcessID=$($process.Id)" | Select-Object -ExpandProperty ExecutablePath)

    #Check if the process path starts with the Chromium folder path
    if($path -and $path.StartsWith($ChromiumFolderPath))
    {
        #If it does, kill the process
        $process | Stop-Process -Force
    }
}

#Kill all chromedriver.exe processes
Get-Process chromedriver -ErrorAction SilentlyContinue | Stop-Process -Force
