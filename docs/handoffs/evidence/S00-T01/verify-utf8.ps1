param(
    [ValidatePattern('^[a-zA-Z0-9/_-]+$')]
    [string]$Ref = 'HEAD'
)

$ErrorActionPreference = 'Stop'
$utf8 = [Text.UTF8Encoding]::new($false, $true)
$paths = @(
    'docs/delivery/BUILD.md',
    'docs/handoffs/S00-T01-coder.md',
    'docs/handoffs/evidence/S00-T01/manual.txt',
    'docs/handoffs/evidence/S00-T01/checks.txt',
    'docs/handoffs/evidence/S00-T01/assembleDebug.txt',
    'docs/handoffs/evidence/S00-T01/clean-build-tests.txt',
    'docs/handoffs/evidence/S00-T01/apk-badging.txt',
    'docs/handoffs/evidence/S00-T01/revalidation-blocker.txt',
    'docs/handoffs/evidence/S00-T01/encoding-verification.json',
    'docs/handoffs/evidence/S00-T01/verify-utf8.ps1'
)

foreach ($path in $paths) {
    $object = $Ref + ':' + $path
    $process = [Diagnostics.Process]::new()
    $process.StartInfo.FileName = 'git'
    $process.StartInfo.Arguments = 'show ' + $object
    $process.StartInfo.UseShellExecute = $false
    $process.StartInfo.RedirectStandardOutput = $true
    $process.StartInfo.RedirectStandardError = $true
    $memory = [IO.MemoryStream]::new()
    try {
        if (!$process.Start()) { throw 'Cannot start git' }
        # Read raw Git bytes, never text re-encoded by the terminal.
        $process.StandardOutput.BaseStream.CopyTo($memory)
        $errorText = $process.StandardError.ReadToEnd()
        $process.WaitForExit()
        if ($process.ExitCode -ne 0) { throw $errorText }
        $text = $utf8.GetString($memory.ToArray())
        if ($text.IndexOf([char]0xFFFD) -ge 0) {
            throw ('Replacement character in ' + $path)
        }
        Write-Output ('PASS UTF-8, zero replacement characters: ' + $path)
    } finally {
        $memory.Dispose()
        $process.Dispose()
    }
}
