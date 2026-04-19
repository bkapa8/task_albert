@echo off
echo Populando BD...
mysql -h localhost -u root -p"" bibliot_uor_db < popular.sql
echo Processo concluido!
pause
