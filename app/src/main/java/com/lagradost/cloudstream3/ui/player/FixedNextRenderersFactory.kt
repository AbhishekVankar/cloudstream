package com.lagradost.cloudstream3.ui.player

import android.content.Context
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioCapabilities
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.text.TextOutput
import androidx.media3.exoplayer.text.TextRenderer
import androidx.preference.PreferenceManager
import com.lagradost.cloudstream3.R
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import java.util.ArrayList

@UnstableApi
class FixedNextRenderersFactory(context: Context) : NextRenderersFactory(context) {
    /** Somehow the nextlib authors decided that we need a text renderer that causes
     * "ERROR_CODE_FAILED_RUNTIME_CHECK".
     *
     * Core issue: https://github.com/anilbeesetti/nextlib/pull/158
     * Comment: https://github.com/recloudstream/cloudstream/pull/2342#issuecomment-3917751718
     * */
    override fun buildTextRenderers(
        context: Context,
        output: TextOutput,
        outputLooper: Looper,
        extensionRendererMode: Int,
        out: ArrayList<Renderer>
    ) {
        out.add(TextRenderer(output, outputLooper))
    }

    override fun buildAudioSink(
        context: Context,
        enableFloatOutput: Boolean,
        enableAudioTrackPlaybackParams: Boolean,
    ): AudioSink? {
        val settingsManager = PreferenceManager.getDefaultSharedPreferences(context)
        val isAudioPassthroughEnabled = settingsManager.getBoolean(
            context.getString(R.string.audio_passthrough_key),
            false
        )

        val builder = DefaultAudioSink.Builder(context)
            .setEnableFloatOutput(enableFloatOutput)
            .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)

        if (isAudioPassthroughEnabled) {
            builder.setAudioCapabilities(AudioCapabilities.getCapabilities(context))
        }

        return builder.build()
    }
}