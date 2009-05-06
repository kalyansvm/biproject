/*
 * Copyright (C) 2005 - 2007 JasperSoft Corporation.  All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from JasperSoft,
 * the following license terms apply:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; and without the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt
 * or write to:
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 */
package com.jaspersoft.jasperserver.war.control;

import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.StreamUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

public class ContentRepositoryFileView extends AbstractView
{
	public static final String REPOSITORY_PATH = "repositoryPath";

	RepositoryService repository;

	public ContentRepositoryFileView(RepositoryService repository)
	{
		this.repository = repository;
	}


	protected void renderMergedOutputModel(Map map, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String pathinfo = request.getPathInfo();
		int start = pathinfo.indexOf('/', 1);
		String  repoPath = pathinfo.substring(start, pathinfo.length());

		if (repoPath == null || repoPath.length() == 0)
			return;

		OutputStream out = response.getOutputStream();

		ContentResource file = (ContentResource) repository.getResource(null, repoPath, ContentResource.class);
		if (file == null) {
			throw new JSException("jsexception.could.not.find.content.resource.with.uri",
					new Object[]{repoPath});
		}
		
		String fileType = file.getFileType();

		if (fileType == null)
			throw new JSException("jsexception.undefined.file.type");

		FileResourceData fileData = repository.getContentResourceData(null, repoPath);

		if (fileType.equals(ContentResource.TYPE_PDF)) {
			response.setContentType("application/pdf");
		}
		else if (fileType.equals(ContentResource.TYPE_XLS)) {
			response.setContentType("application/xls");
			response.setHeader("Content-Disposition", "inline; filename=\"file.xls\"");
		} else if (fileType.equals(ContentResource.TYPE_RTF)) {
			response.setContentType("application/rtf");
		} else if (fileType.equals(ContentResource.TYPE_CSV)) {
			response.setContentType("text/csv");
		}

		if (fileData.hasData()) {
			response.setContentLength(fileData.dataSize());
			StreamUtils.pipeData(fileData.getDataStream(), out);
		} else {
			response.setContentLength(0);
		}		
		
		out.flush();
	}
}
