import React from 'react';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from 'chart.js';
import { Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

/**
 * DoughnutChart component for displaying doughnut charts with customizable options
 * @param {Object} props - Component props
 * @param {Object} props.data - Chart data with labels and datasets
 * @param {Object} props.options - Chart configuration options
 * @param {string} props.height - Chart height (default: '300px')
 */
function DoughnutChart({ data, options = {}, height = '300px' }) {
  const defaultOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          color: '#cbd5e1',
          padding: 15,
          font: {
            size: 12,
          },
        },
      },
      tooltip: {
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        titleColor: '#f1f5f9',
        bodyColor: '#cbd5e1',
        borderColor: '#334155',
        borderWidth: 1,
        padding: 12,
        displayColors: true,
      },
    },
    ...options,
  };

  return (
    <div style={{ height }}>
      <Doughnut data={data} options={defaultOptions} />
    </div>
  );
}

export default DoughnutChart;
