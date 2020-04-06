@echo off
echo NUL>_.class&&del /s /f /q *.class
cls
javac -encoding utf8 -cp com/krzem/fischertechnic_disassembler/modules/jna-5.5.0.jar;com/krzem/fischertechnic_disassembler/modules/jna-platform-5.5.0.jar; com/krzem/fischertechnic_disassembler/Main.java&&java -Dfile.encoding=UTF8 -cp com/krzem/fischertechnic_disassembler/modules/jna-5.5.0.jar;com/krzem/fischertechnic_disassembler/modules/jna-platform-5.5.0.jar; com/krzem/fischertechnic_disassembler/Main temp.rpp
start /min cmd /c "echo NUL>_.class&&del /s /f /q *.class"