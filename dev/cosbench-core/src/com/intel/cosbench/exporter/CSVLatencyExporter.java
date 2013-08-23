/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
*/ 

package com.intel.cosbench.exporter;

import static com.intel.cosbench.exporter.Formats.RATIO;

import java.io.*;

import com.intel.cosbench.bench.*;

/**
 * This class is to export response time histogram data into CSV format.
 * 
 * @author ywang19, qzheng7
 *
 */
class CSVLatencyExporter extends AbstractLatencyExporter {

    @Override
    protected void writeHeader(Writer writer) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("ResTime").append(',');
        for (Metrics metrics : workload.getReport())
            writeOpType(buffer, metrics);
        buffer.setCharAt(buffer.length() - 1, '\n');
        writer.write(buffer.toString());
    }

    private static void writeOpType(StringBuilder buffer, Metrics metrics) {
        String opt = metrics.getOpName();
        String spt = metrics.getSampleType();
        if (spt.equals(opt))
            buffer.append(opt);
        else
        	buffer.append(opt + '-' + spt);
        buffer.append(',').append("(%)").append(',');
    }

    @Override
    protected void writeHistogram(Writer writer, int idx) throws IOException {
        StringBuilder buffer = new StringBuilder();
        long[] resTime = Counter.getResTime(idx);
        buffer.append(resTime[0]).append('~');
        if (resTime[1] < Long.MAX_VALUE)
            buffer.append(resTime[1]);
        else
            buffer.append("+INF");
        buffer.append(',');
        int metricsIdx = 0;
        for (Metrics metrics : workload.getReport()) {
            int count = metrics.getLatency().getHistoData()[idx];
            buffer.append(count).append(',');
            accs[metricsIdx] += count;
            double per = accs[metricsIdx] / ((double) sums[metricsIdx]);
            buffer.append(RATIO.format(per)).append(',');
            metricsIdx++;
        }
        buffer.setCharAt(buffer.length() - 1, '\n');
        writer.write(buffer.toString());
    }

}
