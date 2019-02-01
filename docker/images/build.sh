set -e

cd backend
./build.sh
cd ..

cd frontend
./build.sh
cd ..
