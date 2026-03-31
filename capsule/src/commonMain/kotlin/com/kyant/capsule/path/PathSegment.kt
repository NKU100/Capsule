package com.kyant.capsule.path

import androidx.compose.ui.graphics.Path
import com.kyant.capsule.core.Point
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

sealed interface PathSegment {

    val from: Point
    val to: Point

    fun drawTo(path: Path)

    data class Line(
        override val from: Point,
        override val to: Point
    ) : PathSegment {

        override fun drawTo(path: Path) {
            path.lineTo(to.x.toFloat(), to.y.toFloat())
        }
    }

    data class Arc(
        val center: Point,
        val radius: Double,
        val startAngle: Double,
        val sweepAngle: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(
                center.x + cos(startAngle) * radius,
                center.y + sin(startAngle) * radius
            )

        override val to: Point
            get() = Point(
                center.x + cos(startAngle + sweepAngle) * radius,
                center.y + sin(startAngle + sweepAngle) * radius
            )

        override fun drawTo(path: Path) {
            arcToCubics(path, center, radius, startAngle, sweepAngle)
        }
    }

    data class Circle(
        val center: Point,
        val radius: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(center.x + radius, center.y)

        override val to: Point
            get() = from

        override fun drawTo(path: Path) {
            arcToCubics(path, center, radius, 0.0, PI)
            arcToCubics(path, center, radius, PI, PI)
        }
    }

    data class Cubic(
        val p0: Point,
        val p1: Point,
        val p2: Point,
        val p3: Point
    ) : PathSegment {

        override val from: Point
            get() = p0

        override val to: Point
            get() = p3

        override fun drawTo(path: Path) {
            path.cubicTo(
                p1.x.toFloat(), p1.y.toFloat(),
                p2.x.toFloat(), p2.y.toFloat(),
                p3.x.toFloat(), p3.y.toFloat()
            )
        }
    }
}

/**
 * Approximate a circular arc with cubic Bézier curves.
 * Splits arcs > 90° into segments ≤ 90° each for accuracy.
 */
private fun arcToCubics(
    path: Path,
    center: Point,
    radius: Double,
    startAngle: Double,
    sweepAngle: Double
) {
    if (sweepAngle == 0.0) return
    val maxSegmentAngle = PI / 2.0
    val numSegments = ceil(abs(sweepAngle) / maxSegmentAngle).toInt()
    val segmentAngle = sweepAngle / numSegments
    var angle = startAngle
    for (i in 0 until numSegments) {
        arcSegmentToCubic(path, center, radius, angle, segmentAngle)
        angle += segmentAngle
    }
}

/**
 * Convert a single arc segment (≤ 90°) to a cubic Bézier.
 * Uses the standard tangent-length formula: t = (4/3) * tan(angle/4)
 */
private fun arcSegmentToCubic(
    path: Path,
    center: Point,
    radius: Double,
    startAngle: Double,
    sweepAngle: Double
) {
    val t = (4.0 / 3.0) * tan(sweepAngle / 4.0)
    val cosStart = cos(startAngle)
    val sinStart = sin(startAngle)
    val cosEnd = cos(startAngle + sweepAngle)
    val sinEnd = sin(startAngle + sweepAngle)

    path.cubicTo(
        (center.x + radius * (cosStart - t * sinStart)).toFloat(),
        (center.y + radius * (sinStart + t * cosStart)).toFloat(),
        (center.x + radius * (cosEnd + t * sinEnd)).toFloat(),
        (center.y + radius * (sinEnd - t * cosEnd)).toFloat(),
        (center.x + radius * cosEnd).toFloat(),
        (center.y + radius * sinEnd).toFloat()
    )
}
