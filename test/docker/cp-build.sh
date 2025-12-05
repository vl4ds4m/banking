PROJECT_DIR="../.."
mkdir -p build
for APP in rates converter accounts web-ui transactions; do
  cp "$PROJECT_DIR/$APP/target/$APP.jar" "build/$APP.jar"
done
