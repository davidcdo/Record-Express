package cs371m.dcd954.recordexpress;

import android.widget.ImageButton;

/* This class changes the Image Button for the play/pause Button */
public class ClickPlay implements PlayFrag.TransformImageButton {
    protected boolean onPlay;

    public ClickPlay() {
        onPlay = false;
    }

    public void transform(ImageButton imageButton) {
        if (imageButton == null) {
            return;
        }

        if (onPlay) {
            imageButton.setBackgroundResource(R.drawable.play);
            onPlay = false;
        } else {
            imageButton.setBackgroundResource(R.drawable.pause);
            onPlay = true;
        }
    }
}
