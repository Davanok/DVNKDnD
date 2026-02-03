import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.davanok.dvnkdnd.di.JvmAppGraph
import dev.zacsweers.metro.createGraph
import java.awt.Dimension

fun main() {
    val graph = createGraph<JvmAppGraph>()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DVNKDnD"
        ) {
            with(LocalDensity.current) {
                window.minimumSize = Dimension(640.dp.roundToPx(), 480.dp.roundToPx())
            }
            graph.app(onThemeChanged = {})
        }
    }
}