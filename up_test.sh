docker-compose -p up_test -f docker-compose.test.yml up --build --abort-on-container-exit
EXIT_CODE=$?


echo ""
echo "====================================="

if [ $EXIT_CODE -eq 0 ]; then
    echo "OK Tests Successfully"
else
    echo "ERROR Tests failed with exit code $EXIT_CODE"
fi

echo "====================================="
echo ""
echo "Press any key to exit ..."
read -n 1

# down containers
docker-compose -p up_test -f docker-compose.test.yml down

echo "Contenedores detenidos."

