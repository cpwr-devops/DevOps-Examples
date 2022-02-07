param(
    [string]$workspaceRoot,
    [string]$gitRepo
 )

C:\'Program Files'\Git\cmd\git.exe init $workspaceRoot\tests # timeout=10

C:\'Program Files'\Git\cmd\git.exe --version # timeout=10

CD $workspaceRoot\tests

C:\'Program Files'\Git\cmd\git.exe fetch --tags --progress -- https://github.com/$gitRepo.git +refs/heads/*:refs/remotes/origin/*
C:\'Program Files'\Git\cmd\git.exe config remote.origin.url https://github.com/$gitRepo.git # timeout=10
C:\'Program Files'\Git\cmd\git.exe config --add remote.origin.fetch +refs/heads/*:refs/remotes/origin/* # timeout=10
C:\'Program Files'\Git\cmd\git.exe config remote.origin.url https://github.com/$gitRepo.git # timeout=10
C:\'Program Files'\Git\cmd\git.exe fetch --tags --progress -- https://github.com/$gitRepo.git +refs/heads/*:refs/remotes/origin/*
C:\'Program Files'\Git\cmd\git.exe config core.sparsecheckout # timeout=10
C:\'Program Files'\Git\cmd\git.exe checkout -f master