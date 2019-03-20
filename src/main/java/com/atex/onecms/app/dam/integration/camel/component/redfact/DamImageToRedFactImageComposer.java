package com.atex.onecms.app.dam.integration.camel.component.redfact;

import com.atex.onecms.annotations.Composer;
import com.atex.onecms.app.dam.integration.camel.component.redfact.config.RedfactConfigPolicy;
import com.atex.onecms.app.dam.standard.aspects.OneImageBean;
import com.atex.onecms.content.ContentManager;
import com.atex.onecms.content.ContentResult;
import com.atex.onecms.content.ContentVersionId;
import com.atex.onecms.content.Status;
import com.atex.onecms.content.Subject;
import com.atex.onecms.content.callback.CallbackException;
import com.atex.onecms.content.mapping.ContentComposer;
import com.atex.onecms.content.mapping.Context;
import com.atex.onecms.content.mapping.Request;
import com.atex.onecms.image.ImageInfoAspectBean;
import com.atex.onecms.ws.image.ImageServiceUrlBuilder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Composer used to create a {@link RedFactImageBean} from a {@link OneImageBean}.
 *
 * @author mnova
 */
@Composer(
        variant = "com.atex.onecms.app.dam.integration.camel.component.redfact.image",
        type = "atex.onecms.image",
        variantId = "com.atex.onecms.app.dam.integration.camel.component.redfact.image.variantconfig")
public class DamImageToRedFactImageComposer implements ContentComposer<OneImageBean, RedFactImageBean, Object> {

    @Override
    public ContentResult<RedFactImageBean> compose(final ContentResult<OneImageBean> source,
                                                   final String variant,
                                                   final Request request,
                                                   final Context<Object> context)
            throws CallbackException {

        RedfactConfig redfactConfig = RedfactConfig.getInstance();

        final OneImageBean damImage = source.getContent().getContentData();

        final RedFactImageBean imageBean = new RedFactImageBean();

        imageBean.setContentId(source.getContentId().getContentId());
        imageBean.setPicTitle(damImage.getTitle());
        imageBean.setPicInfo(damImage.getDescription());
        imageBean.setContentId(source.getContentId().getContentId());

        // build the image url which will be a relative url
        // we will make it absolute in the processor.

        final String imageUrl = new ImageServiceUrlBuilder(source.getContent(), redfactConfig.getOneCMSImagePrefix())
                .format(redfactConfig.getImageFormat())
                .buildUrl();

        imageBean.setUrl(imageUrl);

        final ImageInfoAspectBean imageInfo = getImageInfo(context.getContentManager(), source.getContentId());
        if (imageInfo != null)
            imageBean.setPathname(imageInfo.getFilePath());
//        imageBean.setFilePath(checkNotNull(imageInfo, "Image is missing the " + ImageInfoAspectBean.ASPECT_NAME).getFilePath());

        return new ContentResult<>(source, imageBean);
    }

    private ImageInfoAspectBean getImageInfo(final ContentManager contentManager, final ContentVersionId contentId) {
        final ContentResult<Object> cr = contentManager.get(contentId, null, Object.class, null, Subject.NOBODY_CALLER);
        if (cr != null && (cr.getStatus() == Status.OK) && (cr.getContent() != null)) {
            final ImageInfoAspectBean aspect = cr.getContent().getAspectData(ImageInfoAspectBean.ASPECT_NAME);
            return aspect;
        }
        return null;
    }

}
