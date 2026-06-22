#define MyAppName "DGS POS PrintAgent"
#define MyAppVersion "1.0.0"
#define MyPublisher "DTS"

[Setup]
AppId={{ED6E1E57-60CB-44F0-8162-7B89C6040E08}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyPublisher}

DefaultDirName={autopf}\PrintAgent
DefaultGroupName=PrintAgent

OutputDir=output
OutputBaseFilename=PrintAgentSetup

Compression=lzma2
SolidCompression=yes

ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible

PrivilegesRequired=admin

WizardStyle=modern

[Files]
Source: "..\dist\*"; DestDir: "{app}"; Flags: recursesubdirs createallsubdirs ignoreversion

[Run]
Filename: "{app}\PrintAgentService.exe"; Parameters: "install"; Flags: runhidden waituntilterminated
Filename: "{app}\PrintAgentService.exe"; Parameters: "start"; Flags: runhidden waituntilterminated

[UninstallRun]
Filename: "{app}\PrintAgentService.exe"; Parameters: "stop"; Flags: runhidden waituntilterminated
Filename: "{app}\PrintAgentService.exe"; Parameters: "uninstall"; Flags: runhidden waituntilterminated