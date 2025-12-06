# Tests de Pantallas - Level UP

## 📊 Situación Actual: 0% de Cobertura

Los tests actuales en el paquete `pantallas` generan **0% de cobertura** porque:

### ❌ Problema
- Las funciones `@Composable` (pantallas UI) **requieren el runtime de Android** para ejecutarse
- Los tests unitarios (JUnit puro en `/test`) **NO pueden ejecutar código de Android/Compose**
- Por lo tanto, el código de las pantallas nunca se ejecuta → 0% de cobertura

### ✅ Soluciones

#### 1. **Tests de ViewModels** (RECOMENDADO) ⭐
- Los ViewModels contienen la lógica de negocio que usan las pantallas
- SÍ se pueden testear con JUnit puro
- SÍ generan cobertura de código
- **Ver carpeta:** `/test/java/com/example/levelup/viewmodel/`

#### 2. **Tests Instrumentados** (androidTest)
- Tests de UI con Compose Testing Framework
- Requieren un emulador o dispositivo físico
- Son más lentos pero prueban la UI real
- **Ubicación:** `/androidTest/` (no `/test/`)

#### 3. **Robolectric** (complejo)
- Permite ejecutar tests de Android en JUnit
- Más lento y complejo de configurar
- No recomendado para proyectos pequeños

## 🎯 Recomendación

**Para obtener buena cobertura:**

1. ✅ Testea los **ViewModels** (lógica de negocio) → `/test/`
2. ✅ Testea los **Repositorios** (acceso a datos) → `/test/`
3. ✅ Testea **Modelos y Utils** (clases puras) → `/test/`
4. ⚠️ Testea la **UI** solo si es necesario → `/androidTest/`

## 📁 Estructura Recomendada

```
app/src/
├── test/               # Tests unitarios (rápidos, sin Android)
│   ├── viewmodel/     # ✅ Tests de ViewModels
│   ├── repository/    # ✅ Tests de Repositorios
│   ├── model/         # ✅ Tests de Modelos
│   └── pantallas/     # ⚠️ Tests básicos (0% cobertura)
│
└── androidTest/        # Tests instrumentados (lentos, con Android)
    └── ui/            # Tests de UI Compose
```

## 🚀 Ejecutar Tests

```bash
# Tests unitarios (rápidos)
./gradlew testDebugUnitTest

# Reporte de cobertura
./gradlew jacocoTestReport

# Tests instrumentados (requiere emulador)
./gradlew connectedAndroidTest
```

## 📈 Meta de Cobertura

- ViewModels: 80%+
- Repositorios: 70%+
- Modelos: 90%+
- UI (Compose): 0-20% (normal en unit tests)
