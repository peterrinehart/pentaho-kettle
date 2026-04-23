/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.di.trans.steps.fileinput.text;

import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.steps.file.BaseFileInputAdditionalField;
import org.pentaho.di.trans.steps.file.IBaseFileInputReader;
import org.pentaho.di.trans.steps.file.IBaseFileInputStepControl;
import org.pentaho.di.trans.step.errorhandling.FileErrorHandler;

import java.util.Date;

/**
 * Provides CSV parsing behavior for Text File Input.
 */
public interface TextFileInputCsvReaderProvider {

  String getId();

  String getDisplayName();

  IBaseFileInputReader createReader( IBaseFileInputStepControl step, TextFileInputMeta meta, TextFileInputData data,
                                     FileObject file, LogChannelInterface log ) throws Exception;

  String[] guessStringsFromLine( VariableSpace space, LogChannelInterface log, String line, TextFileInputMeta meta,
                                 String delimiter, String enclosure, String escapeCharacter ) throws KettleException;

  TextFileLine getLine( LogChannelInterface log, BufferedInputStreamReader reader, EncodingType encodingType,
                        int fileFormatType, StringBuilder line, String delimiter, String enclosure,
                        String escapeCharacter, long lineNumberInFile ) throws KettleFileException;

  long skipLines( LogChannelInterface log, BufferedInputStreamReader reader, EncodingType encodingType,
                  int fileFormatType, StringBuilder line, int nrLinesToSkip, String delimiter,
                  String enclosure, String escapeCharacter, long lineNumberInFile ) throws KettleFileException;

  Object[] convertLineToRow( LogChannelInterface log, TextFileLine textFileLine, TextFileInputMeta meta,
                             Object[] passThruFields, int nrPassThruFields, RowMetaInterface outputRowMeta,
                             RowMetaInterface convertRowMeta, String fname, long rowNr, String delimiter,
                             String enclosure, String escapeCharacter, FileErrorHandler errorHandler,
                             BaseFileInputAdditionalField additionalOutputFields, String shortFilename, String path,
                             boolean hidden, Date modificationDateTime, String uri, String rooturi,
                             String extension, Long size, boolean failOnParseError ) throws KettleException;
}

