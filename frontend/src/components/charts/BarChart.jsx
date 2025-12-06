import React from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

/**
 * BarChart component for displaying bar charts with customizable options
 * @param {Object} props - Component props
 * @param {Object} props.data - Chart data with labels and datasets
 * @param {Object} props.options - Chart configuration options
 * @param {string} props.height - Chart height (default: '300px')
 */
function BarChart({ data, options = {}, height = '300px' }) {
  const defaultOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
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
    scales: {
      x: {
        grid: {
          color: 'rgba(51, 65, 85, 0.3)',
        },
        ticks: {
          color: '#94a3b8',
        },
      },
      y: {
        grid: {
          color: 'rgba(51, 65, 85, 0.3)',
        },
        ticks: {
          color: '#94a3b8',
        },
      },
    },
    ...options,
  };

  return (
    <div style={{ height }}>
      <Bar data={data} options={defaultOptions} />
    </div>
  );
}

export default BarChart;
